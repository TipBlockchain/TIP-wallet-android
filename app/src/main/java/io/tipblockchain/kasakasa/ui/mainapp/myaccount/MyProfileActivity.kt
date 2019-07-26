package io.tipblockchain.kasakasa.ui.mainapp.myaccount

import android.os.Bundle
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_settings.*

class MyProfileActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

}
