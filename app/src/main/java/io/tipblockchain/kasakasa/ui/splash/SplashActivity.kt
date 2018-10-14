package io.tipblockchain.kasakasa.ui.splash

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.mainapp.MainTabActivity
import io.tipblockchain.kasakasa.ui.onboarding.OnboardingActivity

class SplashActivity : BaseActivity(), SplashView{

    lateinit var presenter: SplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        presenter = SplashPresenter(this.application, this)
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun gotoOnboarding() {
        Log.d(LOG_TAG, "Going to onboarding")
        val intent = Intent(this, OnboardingActivity::class.java)
        startActivity(intent)
    }

    override fun gotoMainApp() {
        Log.d(LOG_TAG, "Going to main app")
        val intent = Intent(this, MainTabActivity::class.java)
        startActivity(intent)
    }
}
