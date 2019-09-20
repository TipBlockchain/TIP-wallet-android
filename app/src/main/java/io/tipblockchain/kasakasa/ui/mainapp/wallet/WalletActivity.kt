package io.tipblockchain.kasakasa.ui.mainapp.wallet

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.data.db.entity.Wallet

class WalletActivity : AppCompatActivity() {

    var wallet: Wallet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extra = intent.getSerializableExtra("wallet")
        if (extra != null) {
            val wallet = extra as Wallet
            this.wallet = wallet
        }
        setContentView(R.layout.activity_wallet)
    }

    fun getData(): Bundle {
        val bundle = Bundle()
        if (wallet != null) {
            bundle.putSerializable("wallet", wallet!!)
        }

        return bundle
    }
}
