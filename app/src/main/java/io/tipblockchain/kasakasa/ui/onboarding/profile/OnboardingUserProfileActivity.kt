package io.tipblockchain.kasakasa.ui.onboarding.profile

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.os.Bundle
import io.tipblockchain.kasakasa.R
import kotlinx.android.synthetic.main.activity_onboarding_user_profile.*
import kotlinx.android.synthetic.main.content_onboarding_user_profile.*
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository
import io.tipblockchain.kasakasa.data.responses.Authorization
import io.tipblockchain.kasakasa.databinding.ActivityOnboardingUserProfileBinding
import io.tipblockchain.kasakasa.extensions.onTextChange
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.mainapp.MainTabActivity
import io.tipblockchain.kasakasa.ui.onboarding.password.ChoosePasswordActivity
import io.tipblockchain.kasakasa.ui.onboarding.verifyphone.VerifyPhoneNumberActivity
import io.tipblockchain.kasakasa.utils.FileUtils
import io.tipblockchain.kasakasa.utils.KeyboardUtils
import java.io.File
import java.lang.Exception


class OnboardingUserProfileActivity : BaseActivity(), OnboardingUserProfile.View {

    private lateinit var viewModel: OnboardingUserProfileViewModel
    private var permissionsGranted: Boolean = false
    private var presenter: OnboardingUserProfile.Presenter? = null
    private val walletRepository = WalletRepository.instance
    private var displayPicFile: File? = null
    private var demoAccountExists = false

    private enum class ActivityRequest(val code: Int) {
        CAMERA(111),
        GALLERY(112),
        CROP(UCrop.REQUEST_CROP)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_user_profile)

        val binding: ActivityOnboardingUserProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding_user_profile)
        viewModel = getViewModel()
        binding.viewModel = viewModel

        this.setupPresenter()
        nextBtn.setOnClickListener { nextButtonClicked() }
        usernameTv.onTextChange { checkUsername(it) }
        cameraImageButton.setOnClickListener { checkPermissions() }
    }

    private fun setupPresenter() {
        presenter = OnboardingUserProfilePresenter()
        presenter?.viewModel = viewModel
        presenter?.attach(this)
        presenter?.checkForDemoAccount()

        walletRepository.primaryWallet().observe(this, Observer { wallet ->
            if (wallet != null) {
                presenter?.wallet = wallet
            } else {
                onWalletNotSetupError()
            }
        })
    }

    override fun onDestroy() {
        viewModel.destroy()
        presenter?.detach()
        super.onDestroy()
    }


    private fun navigateToCreateWallet() {
        val intent = Intent(this, ChoosePasswordActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or intent.flags
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        when (requestCode) {
            ActivityRequest.CAMERA.code -> if (resultCode == Activity.RESULT_OK) {
                (imageReturnedIntent!!.data)?.let { showImageCropper(it) }
            }
            ActivityRequest.GALLERY.code -> if (resultCode == Activity.RESULT_OK) {
                (imageReturnedIntent!!.data)?.let { showImageCropper(it) }
            }
            ActivityRequest.CROP.code -> if (resultCode == Activity.RESULT_OK) {
                val croppedImageUri = UCrop.getOutput(imageReturnedIntent!!)
                profileImageView.setImageURI(croppedImageUri)
                try {
                    displayPicFile = File(croppedImageUri?.path)
                } catch (e: Exception) {}
            } else {
                val cropError = UCrop.getError(imageReturnedIntent!!)
                showOkDialog(cropError?.localizedMessage ?: getString(R.string.generic_error))
            }
        }
    }

    private fun showImageCropper(uri:Uri) {
        val destinationFilename = uri.lastPathSegment?.let { it + "_cropped" } ?: return
        val destinationUri: Uri = Uri.fromFile(FileUtils().imageFileWithName(destinationFilename))
        val options = UCrop.Options()

        options.setStatusBarColor(this.getColorFromId(R.color.colorPink))
        options.setToolbarColor(this.getColorFromId(R.color.colorPink))
        options.setActiveWidgetColor(this.getColorFromId(R.color.colorAppPurple))
        UCrop.of(uri, destinationUri).withAspectRatio(1.0f, 1.0f).withMaxResultSize(500, 500).withOptions(options).start(this)
    }

    private fun checkPermissions() {
        if (permissionsGranted) {
            showPictureDialog()
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener (object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report?.areAllPermissionsGranted() == true) {
                            showPictureDialog()
                        } else if (report?.isAnyPermissionPermanentlyDenied ?: false) {
                            showPermissionsDeniedDialog()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                        token?.continuePermissionRequest()
                    }
                }).check()
    }

    private fun showPermissionsDeniedDialog() {
        this.showOkCancelDialog(getString(R.string.title_allow_camera_access), getString(R.string.permission_camera_denied), onClickListener = object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                when(which) {
                    DialogInterface.BUTTON_POSITIVE ->  requestPermissions()
                }
            }
        })
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems) { _, which ->
            when (which) {
                0 -> pickPhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun takePhotoFromCamera() {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePicture, ActivityRequest.CAMERA.code)
    }

    private fun pickPhotoFromGallery() {
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhotoIntent , ActivityRequest.GALLERY.code)
    }

    private fun checkUsername(username: String) {
        presenter?.checkUsername(username)
    }

    private fun saveViewModel() {
        viewModel.firstname = firstnameTv.text.toString()
        viewModel.lastname = lastnameTv.text.toString()
        viewModel.username = usernameTv.text.toString()
        viewModel.profilePhoto = imageView.drawable as BitmapDrawable?
        presenter?.viewModel = viewModel
    }

    fun nextButtonClicked() {
        KeyboardUtils.hideKeyboard(this)
        saveViewModel()
        this.checkValues()
    }

    override fun onDemoAccountFound(demoUser: User) {
        demoAccountExists = true

        viewModel.firstname = demoUser.firstname().trim()
        viewModel.lastname = demoUser.lastname().trim()
        viewModel.username = demoUser.username.trim()

        usernameTv.isEnabled = false
        usernameTv.isFocusable = false
        usernameTv.isCursorVisible = false

        if (demoUser.originalPhotoUrl != null) {
            Picasso.get().load(demoUser.originalPhotoUrl).into(profileImageView)
        } else {
            Picasso.get().load(R.drawable.avatar_placeholder_small).into(profileImageView)
        }
    }

    override fun onPhotoUploaded() {
        this.showCongratsDialog()
    }

    override fun onErrorUpdatingUser(error: Throwable) {
        showOkDialog(getString(R.string.error_updating_user_info, error.localizedMessage))
    }

    override fun onSignupTokenError() {
        showOkDialog(getString(R.string.error_session_timeout_reconfirm), onClickListener = object: DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                navigateToConfirmPhoneNumber()
            }
        })
    }

    override fun onAuthorizationFetched(auth: Authorization?, error: Throwable?) {
        KeyboardUtils.hideKeyboard(this)
        if (error != null) {
            showOkDialog(getString(R.string.error_creating_account, error.localizedMessage), onClickListener = object: DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    finish()
                }
            })
        } else {
            if (displayPicFile != null) {
                presenter?.uploadPhoto(displayPicFile!!)
            } else {
                showCongratsDialog()
            }

        }
    }

    private fun showCongratsDialog() {
        showOkDialog(getString(R.string.congrats_account_created), onClickListener = object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                navigateToMainApp()
            }
        })
    }
    override fun onGenericError(error: Throwable) {
        showMessage(error.localizedMessage)
    }

    override fun onUsernameAvailable() {
        usernameTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkmark_green, 0);
    }

    override fun onUsernameUnavailableError(isDemoAccount: Boolean) {
        if (!isDemoAccount || (isDemoAccount && !demoAccountExists)) {
            usernameTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            usernameTv.error = getString(R.string.error_username_unavailable)
            usernameTv.requestFocus()
        }
    }

    override fun onInvalidUser() {
        showMessage(getString(R.string.invalid_user))
    }

    override fun onWalletNotSetupError() {
        showOkDialog(getString(R.string.error_no_primary_wallet), onClickListener = object: DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                navigateToCreateWallet()
            }
        })
    }

    private fun getViewModel() = ViewModelProviders.of(this).get(OnboardingUserProfileViewModel::class.java)

    private fun checkValues() {
        firstnameTv.error = null
        lastnameTv.error = null
        usernameTv.error = null

        var focusView: View? = null
        var cancel = false

        if (TextUtils.isEmpty(viewModel.firstname)) {
            focusView = firstnameTv
            firstnameTv.error = getString(R.string.error_firstname_empty)
            cancel = true
        } else if (TextUtils.isEmpty(viewModel.username)) {
            focusView = usernameTv
            usernameTv.error = getString(R.string.error_username_empty)
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            this.createAccount()
        }
    }

    private fun createAccount() {
        presenter?.createAccount()
    }

    private fun navigateToMainApp() {
        val intent = Intent(this, MainTabActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToConfirmPhoneNumber() {
        val intent = Intent(this, VerifyPhoneNumberActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}

