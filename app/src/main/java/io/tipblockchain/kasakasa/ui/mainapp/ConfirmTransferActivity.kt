package io.tipblockchain.kasakasa.ui.mainapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.ui.BaseActivity

import kotlinx.android.synthetic.main.activity_confirm_transaction.*

class ConfirmTransferActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_transaction)

        val tx = intent.getSerializableExtra("transaction")
        Log.d(LOG_TAG, "tx = $tx")
        confirmBtn.setOnClickListener { navigateToTransactionConfirmed() }
    }


    fun navigateToTransactionConfirmed() {
        val intent = Intent(this, TransactionConfirmedActivity::class.java)
        intent.putExtra("keyIdentifier", "value")
        startActivity(intent)
    }

}
