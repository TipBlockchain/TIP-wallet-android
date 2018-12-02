package io.tipblockchain.kasakasa.ui.mainapp.myaccount

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop

import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.app.AppConstants
import io.tipblockchain.kasakasa.config.AppProperties
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.BaseFragment
import io.tipblockchain.kasakasa.utils.FileUtils
import kotlinx.android.synthetic.main.fragment_my_account.*
import java.io.File

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * [MyAccountFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MyAccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyAccountFragment : BaseFragment(), MyAccount.View {

    var permissionsGranted = false

    private enum class ActivityRequest(val code: Int) {
        CAMERA(111),
        GALLERY(112),
        CROP(UCrop.REQUEST_CROP)
    }

    private var mListener: OnFragmentInteractionListener? = null
    var presenter: MyAccount.Presenter? = null
    var baseActivity: BaseActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = MyAccountPresenter()
        presenter?.attach(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        baseActivity = activity as BaseActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_account, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter?.loadUser()
        cameraImageButton.setOnClickListener { checkPermissions() }
        buyTipBtn.setOnClickListener { openBuyTipUrl() }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        presenter?.detach()
        super.onDestroy()
    }
    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun updateUser(user: User) {
        usernameTv.setText(user.username)
        if (user.originalPhotoUrl != null) {
            Picasso.get().load(user.originalPhotoUrl).into(profileImageView)
        } else {
            Picasso.get().load(R.drawable.avatar_placeholder_small).into(profileImageView)
        }
    }

    override fun onErrorUpdatingUser(error: Throwable) {
        showOkDialog(getString(R.string.error_updating_user_info, error.localizedMessage))
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
                if (croppedImageUri != null) {
                    profileImageView.setImageURI(croppedImageUri)
                    val imageFile = File(croppedImageUri.path)
                    presenter?.uploadPhoto(imageFile)
                }
            } else {
                val cropError = UCrop.getError(imageReturnedIntent!!)
                showOkDialog(cropError?.localizedMessage ?: getString(R.string.generic_error))
            }
        }
    }

    private fun showImageCropper(uri:Uri) {
        if (activity == null) {
            return
        }
        val destinationFilename = uri.lastPathSegment?.let { it + "_cropped" } ?: return
        val destinationUri: Uri = Uri.fromFile(FileUtils().imageFileWithName(destinationFilename))
        val options = UCrop.Options()

        options.setStatusBarColor(this.getColorFromId(R.color.colorPink))
        options.setToolbarColor(this.getColorFromId(R.color.colorPink))
        options.setActiveWidgetColor(this.getColorFromId(R.color.colorAppPurple))
        UCrop.of(uri, destinationUri).withAspectRatio(1.0f, 1.0f).withMaxResultSize(500, 500).withOptions(options).start(activity!!, this)
    }

    private fun checkPermissions() {
        if (permissionsGranted) {
            showPictureDialog()
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        if (activity == null) {
            return
        }
        Dexter.withActivity(activity!!)
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
        if (activity == null) {
            return
        }

        val pictureDialog = AlertDialog.Builder(activity!!)

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
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhotoIntent , ActivityRequest.GALLERY.code)
    }

    private fun openBuyTipUrl() {
        val url = AppProperties.get(AppConstants.BUY_TIP_URL)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        // The request code must be 0 or greater.

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyAccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String): MyAccountFragment {
            val fragment = MyAccountFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            fragment.arguments = args
            return fragment
        }
    }
}
