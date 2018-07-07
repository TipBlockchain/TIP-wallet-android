package io.tipblockchain.kasakasaprototype.ui.backup

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import io.tipblockchain.kasakasaprototype.R
import io.tipblockchain.kasakasaprototype.ui.newaccount.ChooseUsernameActivity

import kotlinx.android.synthetic.main.activity_backup_info.*

class BackupInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup_info)

        button.setOnClickListener {
            goToMainBackup()
        }
    }


    private fun goToMainBackup() {
        val intent = Intent(this, BackupAccountActivity::class.java)
        intent.putExtra("keyIdentifier", "value")
        startActivity(intent)
    }

}
