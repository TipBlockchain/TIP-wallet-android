package io.tipblockchain.kasakasa.ui.onboarding.profile

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import io.tipblockchain.kasakasa.R
import kotlinx.android.synthetic.main.activity_onboarding_user_profile.*
import kotlinx.android.synthetic.main.content_onboarding_user_profile.*
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import io.tipblockchain.kasakasa.databinding.ActivityOnboardingUserProfileBinding
import io.tipblockchain.kasakasa.ui.mainapp.MainTabActivity


class OnboardingUserProfileActivity : AppCompatActivity(), OnboardignUserProfileView {

    private lateinit var viewModel: OnboardingUserProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_user_profile)
        setSupportActionBar(toolbar)

        val binding: ActivityOnboardingUserProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding_user_profile)
        viewModel = getViewModel()
        binding.viewModel = viewModel

        nextBtn.setOnClickListener {
            nextButtonClicked()
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        when (requestCode) {
            0 -> if (resultCode == Activity.RESULT_OK) {
                val selectedImage = imageReturnedIntent!!.data
                imageView.setImageURI(selectedImage)
            }
            1 -> if (resultCode == Activity.RESULT_OK) {
                val selectedImage = imageReturnedIntent!!.data
                imageView.setImageURI(selectedImage)
            }
        }
    }

    private fun takePhoto() {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePicture, 0)
    }

    private fun pickPhoto() {
        val pickPhotoIntent = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent , 1)
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
        var cancel: Boolean = false

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
