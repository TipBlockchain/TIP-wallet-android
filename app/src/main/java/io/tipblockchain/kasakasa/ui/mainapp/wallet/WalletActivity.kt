package io.tipblockchain.kasakasa.ui.mainapp.wallet

import android.os.Bundle
import android.util.Log
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_wallet.*

class WalletActivity : BaseActivity() {

    var wallet: Wallet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extra = intent.getSerializableExtra("wallet")
        if (extra != null) {
            val wallet = extra as Wallet
            this.wallet = wallet
        }
        setContentView(R.layout.activity_wallet)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun getData(): Bundle {
        val bundle = Bundle()
        if (wallet != null) {
            bundle.putSerializable("wallet", wallet!!)

            fragment.arguments = bundle

            Log.i(LOG_TAG, "fragment arguments are $bundle")
        }

        return bundle
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
