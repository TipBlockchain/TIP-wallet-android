package io.tipblockchain.kasakasa.ui.splash

import android.arch.lifecycle.Observer
import android.content.*
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.app.App
import io.tipblockchain.kasakasa.app.AppConstants
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.mainapp.MainTabActivity
import io.tipblockchain.kasakasa.ui.onboarding.OnboardingActivity
import io.tipblockchain.kasakasa.ui.onboarding.enterphone.EnterPhoneNumberActivity

class SplashActivity : BaseActivity(), SplashScreenContract.View {

    private lateinit var presenter: SplashPresenter
    private var mainAppLaunched = false
    private var onboardingLaunched = false
    private val receiver = SplashReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.registerReceiver()
        setContentView(R.layout.activity_splash)
        App.application().fetchProperties()
        this.setupPresenter()
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }

    override fun gotoOnboarding() {
        if (onboardingLaunched) {
            return
        }
        onboardingLaunched = true
        val intent = Intent(this, OnboardingActivity::class.java)
        intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun gotoMainApp() {
        if (mainAppLaunched) {
            return
        }
        mainAppLaunched = true
        val intent = Intent(this, MainTabActivity::class.java)
        intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    fun gotoSignup() {
        val intent = Intent(this, EnterPhoneNumberActivity::class.java)
        intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
    private fun proceed() {

    }

    private fun setupPresenter() {
        presenter = SplashPresenter()
        presenter.attach(this)
    }

    private fun loadWallet() {
        val walletRepository = WalletRepository.instance
        walletRepository.primaryWallet().observe(this, Observer {wallet ->
            presenter.walletFetched(wallet)
        })

    }

    private fun registerReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(AppConstants.ACTION_CONFIG_LOADED)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)
    }

    private fun showConfigErrorDialog() {
        showOkDialog(title = getString(R.string.error_connecting_network), message = getString(R.string.error_connecting_network_message), onClickListener = DialogInterface.OnClickListener { _, _ ->
            finish()
        })
    }

    inner class SplashReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(LOG_TAG, "Action received")
            if (intent != null && intent.action == AppConstants.ACTION_CONFIG_LOADED) {
                val configLoaddedSuccessFully = intent.getBooleanExtra(AppConstants.EXTRA_CONFIG_LOADED, false)
                if (configLoaddedSuccessFully) {
                    loadWallet()
                } else {
                    showConfigErrorDialog()
                }
            }
        }
    }
}
