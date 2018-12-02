package io.tipblockchain.kasakasa.ui.backup

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.tipblockchain.kasakasa.R

import kotlinx.android.synthetic.main.activity_backup_info.*

class BackupInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup_info)

        confirmBtn.setOnClickListener {
            goToMainBackup()
        }
    }


    private fun goToMainBackup() {
        val intent = Intent(this, BackupAccountActivity::class.java)
        startActivity(intent)
    }

}
