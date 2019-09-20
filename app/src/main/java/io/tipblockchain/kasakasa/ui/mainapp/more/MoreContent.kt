package io.tipblockchain.kasakasa.ui.mainapp.more
import android.content.Context
import android.content.Intent
import android.net.Uri
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.app.AppConstants
import io.tipblockchain.kasakasa.config.AppProperties
import io.tipblockchain.kasakasa.ui.mainapp.myaccount.MyProfileActivity
import io.tipblockchain.kasakasa.ui.mainapp.sendtransfer.SendTransferActivity
import io.tipblockchain.kasakasa.ui.settings.SettingsActivity
import java.net.URL

class MoreContent {

    companion object {
        var context: Context? = null

        val items: List<MoreListItem> = listOf(
                MoreListItem("My Account", null, true),
                MoreListItem("My Profile", R.drawable.ic_user, action = {
                    val intent = Intent(context, MyProfileActivity::class.java)
                    context?.startActivity(intent)
                }),
                MoreListItem("Settings", R.drawable.ic_settings, action = {
                    val intent = Intent(context, SettingsActivity::class.java)
                    context?.startActivity(intent)
                }),

                MoreListItem("Trade TIP", null, true),
                MoreListItem("Trade TIP", R.drawable.ic_chart),

                MoreListItem("Join Our Community", null, true),
                MoreListItem("Telegram", R.drawable.ic_telegram, action = {
                    openUrl("https://t.me/TipBLockchain")
                }),
                MoreListItem("Twitter", R.drawable.ic_twitter, action = {
                    openUrl("https://twitter.com")
                }),
                MoreListItem("Facebook", R.drawable.ic_facebook, action = {
                    openUrl("https://facebook.com/tipnetworkio")
                }),
                MoreListItem("Reddit", R.drawable.ic_reddit, action = {
                    openUrl("https://reddit.com")
                }),

                MoreListItem("Sign Out", null, true),
                MoreListItem("Sign Out", R.drawable.ic_exit),
                MoreListItem("", null, isHeader = true)
        )


        fun openUrl(url: String) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            context?.startActivity(intent)
        }

    }

}