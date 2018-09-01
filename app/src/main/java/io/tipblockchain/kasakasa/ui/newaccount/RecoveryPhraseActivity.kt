package io.tipblockchain.kasakasa.ui.newaccount

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import io.tipblockchain.kasakasa.R

import kotlinx.android.synthetic.main.activity_recovery_phrase.*

class RecoveryPhraseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovery_phrase)
//        setSupportActionBar(toolbar)

        verifyBtn.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

}

interface RecoveryPhraseView {
}