package io.tipblockchain.kasakasa.ui.mainapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.tipblockchain.kasakasa.R
import kotlinx.android.synthetic.main.activity_backup_info.*

class TransactionConfirmedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_confirmed)
        confirmBtn.setOnClickListener { navigateToMainApp() }
    }

    fun navigateToMainApp() {
        val intent = Intent(this, MainTabActivity::class.java)
        startActivity(intent)
    }
}
