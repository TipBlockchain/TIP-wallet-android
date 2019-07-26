package io.tipblockchain.kasakasa.ui.settings

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.ui.BaseActivity

import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        this.showBackButton(toolbar)
    }

}
