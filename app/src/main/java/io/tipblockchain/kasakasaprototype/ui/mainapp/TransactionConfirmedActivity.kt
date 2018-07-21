package io.tipblockchain.kasakasaprototype.ui.mainapp

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import io.tipblockchain.kasakasaprototype.R
import kotlinx.android.synthetic.main.activity_backup_info.*

class TransactionConfirmedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_confirmed)
        confirmBtn.setOnClickListener { navigateToMainApp() }

    }

    fun navigateToMainApp() {
        val intent = Intent(this, MainTabActivity::class.java)
        intent.putExtra("keyIdentifier", "value")
        startActivity(intent)
    }
}
