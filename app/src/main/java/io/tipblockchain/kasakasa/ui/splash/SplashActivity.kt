package io.tipblockchain.kasakasa.ui.splash

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.app.App
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.mainapp.MainTabActivity
import io.tipblockchain.kasakasa.ui.onboarding.OnboardingActivity

class SplashActivity : BaseActivity(), SplashScreenContract.View {

    private lateinit var presenter: SplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        this.setupPresenter()
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }

    override fun gotoOnboarding() {
        val intent = Intent(this, OnboardingActivity::class.java)
        startActivity(intent)
    }

    override fun gotoMainApp() {
        val intent = Intent(this, MainTabActivity::class.java)
        startActivity(intent)
    }

    private fun setupPresenter() {
        val walletRepository = WalletRepository(App.application())
        presenter = SplashPresenter(this.application)
        presenter.attach(this)

        walletRepository.primaryWallet().observe(this, Observer {wallet ->
            presenter.walletFetched(wallet)
        })
    }
}
