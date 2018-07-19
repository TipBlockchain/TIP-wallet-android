package io.tipblockchain.kasakasaprototype.ui.backup

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.tipblockchain.kasakasaprototype.R
import io.tipblockchain.kasakasaprototype.ui.mainapp.MainTabActivity

import kotlinx.android.synthetic.main.activity_backup_finished.*

class BackupFinishedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup_finished)
        okayBtn.setOnClickListener {
            goToMainApp()
        }
    }

    private fun goToMainApp() {
        val intent = Intent(this, MainTabActivity::class.java)
        intent.putExtra("keyIdentifier", "value")
        startActivity(intent)

        val prefs = this.getSharedPreferences(getString(R.string.default_prefs_file), 0)
        var editor = prefs.edit()
        editor.putBoolean(getString(R.string.prefs_setup_complete), true)
        editor.apply()
    }

}
