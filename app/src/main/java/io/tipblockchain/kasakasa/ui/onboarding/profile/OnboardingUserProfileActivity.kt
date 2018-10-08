package io.tipblockchain.kasakasa.ui.onboarding.profile

import android.Manifest
import android.app.Activity
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
import android.util.Log
import android.view.View
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.yalantis.ucrop.UCrop
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.tipblockchain.kasakasa.data.responses.UsernameResponse
import io.tipblockchain.kasakasa.databinding.ActivityOnboardingUserProfileBinding
import io.tipblockchain.kasakasa.extensions.onTextChange
import io.tipblockchain.kasakasa.networking.TipApiService
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.mainapp.MainTabActivity
import io.tipblockchain.kasakasa.utils.FileUtils
import java.util.concurrent.TimeUnit


class OnboardingUserProfileActivity : BaseActivity(), OnboardignUserProfileView {

    private lateinit var viewModel: OnboardingUserProfileViewModel
    private var checkUsernameDisposable: Disposable? = null
    private var usernameDisposable: Disposable? = null
    private var usernameSubject: PublishSubject<String>? = null
    private var permissionsGranted: Boolean = false

    private enum class ActivityRequest(val code: Int) {
        CAMERA(111),
        GALLERY(112),
        CROP(UCrop.REQUEST_CROP)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_user_profile)
        setSupportActionBar(toolbar)

        val binding: ActivityOnboardingUserProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding_user_profile)
        viewModel = getViewModel()
        binding.viewModel = viewModel

        setupUsernameSubject()

        nextBtn.setOnClickListener { nextButtonClicked() }
        usernameTv.onTextChange { checkUsernameDelayed(it) }
        cameraImageButton.setOnClickListener { checkPermissions() }

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
            } else {
                val cropError = UCrop.getError(imageReturnedIntent!!)
                showOkDialog(cropError?.localizedMessage ?: "An error occured")
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

    private fun setupUsernameSubject() {
        usernameSubject = PublishSubject.create()
        checkUsernameDisposable = usernameSubject!!.flatMap { TipApiService().checkUsername(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn { UsernameResponse(getString(R.string.error_username_unavailable), false) }
                .subscribe {
                    if (!it.isAvailable) {
                        usernameTv.error = getString(R.string.error_username_unavailable)
                    }
                }
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
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                        token?.continuePermissionRequest()
                    }
                }).check()
    }

    fun showPermissionsDialog() {
        this.showOkCancelDialog(getString(R.string.permission_request_camera_storage), DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> requestPermissions()
                DialogInterface.BUTTON_NEGATIVE -> showPermissionsDeniedDialog()
            }
        })
    }

    private fun showPermissionsDeniedDialog() {
        this.showOkDialog(getString(R.string.permission_camera_denied))
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

    private fun checkUsernameDelayed(username: String) {
        usernameDisposable?.dispose()
        // Wait 1.5 seconds after user types before making a request, so we don't make unnecessary requests
        usernameDisposable = Completable.timer(1500, TimeUnit.MILLISECONDS, Schedulers.io())
                .subscribe{
                    usernameSubject?.onNext(username)
                }
    }

    private fun saveViewModel() {
        viewModel.firstname = firstnameTv.text.toString()
        viewModel.lastname = lastnameTv.text.toString()
        viewModel.username = usernameTv.text.toString()
        viewModel.profilePhoto = imageView.drawable as BitmapDrawable?
    }

    override fun usernameEntered() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun firstnameEtnered() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun lastnameEntered() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun profilePhotoSelected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun nextButtonClicked() {
        Log.d(LOG_TAG, "next")
        this.checkValues()
    }

    private fun navigateToNextScreen() {
        val intent = Intent(this, MainTabActivity::class.java)
        startActivity(intent)
    }

    private fun getViewModel() = ViewModelProviders.of(this).get(OnboardingUserProfileViewModel::class.java)

    private fun checkValues() {
        saveViewModel()
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
            this.navigateToNextScreen()
        }
    }
}
