package io.tipblockchain.kasakasa.ui.mainapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.View
import io.tipblockchain.kasakasa.R

import kotlinx.android.synthetic.main.activity_confirm_transaction.*

class ConfirmTransferActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_transaction)

        confirmBtn.setOnClickListener { navigateToTransactionConfirmed() }


    }


    fun navigateToTransactionConfirmed() {
        val intent = Intent(this, TransactionConfirmedActivity::class.java)
        intent.putExtra("keyIdentifier", "value")
        startActivity(intent)
    }

}
