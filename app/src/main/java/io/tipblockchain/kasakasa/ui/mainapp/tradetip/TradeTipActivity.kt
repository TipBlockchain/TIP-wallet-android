package io.tipblockchain.kasakasa.ui.mainapp.tradetip

import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.ui.BaseActivity

import kotlinx.android.synthetic.main.activity_trade_tip.*

class TradeTipActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trade_tip)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

}
