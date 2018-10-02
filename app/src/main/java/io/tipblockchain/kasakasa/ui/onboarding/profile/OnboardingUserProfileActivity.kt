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
import io.tipblockchain.kasakasa.databinding.ActivityOnboardingUserProfileBinding


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
        this.saveViewModel()
        if (viewModel.canProceed()) {
            this.navigateToNextScreen()
        }
    }

    private fun navigateToNextScreen() {

    }

    private fun getViewModel() = ViewModelProviders.of(this).get(OnboardingUserProfileViewModel::class.java)

}
