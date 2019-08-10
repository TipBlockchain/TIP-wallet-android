package io.tipblockchain.kasakasa.ui.mainapp.more
import android.content.Context
import android.content.Intent
import android.net.Uri
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.ui.mainapp.myaccount.MyProfileActivity
import io.tipblockchain.kasakasa.ui.settings.SettingsActivity
import android.support.v4.content.ContextCompat.startActivity
import io.tipblockchain.kasakasa.ui.mainapp.tradetip.TradeTipActivity

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
                MoreListItem("Trade TIP", R.drawable.ic_chart, action = {
                    val intent = Intent(context, TradeTipActivity::class.java)
                    context?.startActivity(intent)
                }),

                MoreListItem("Join Our Community", null, true),
                MoreListItem("Telegram", R.drawable.ic_telegram, action = {
                    openUrl("https://t.me/TipBLockchain")
                }),
                MoreListItem("Twitter", R.drawable.ic_twitter, action = {
                    openUrl("https://twitter.com/TipBlockchain")
                }),
                MoreListItem("Facebook", R.drawable.ic_facebook, action = {
                    openUrl("https://facebook.com/tipnetworkio")
                }),
                MoreListItem("Reddit", R.drawable.ic_reddit, action = {
                    openUrl("https://www.reddit.com/r/TipBlockchain/")
                }),
                MoreListItem("Invite Friends", R.drawable.ic_share, action = {
                    val i = Intent(Intent.ACTION_SEND)
                    i.type = "text/plain"
                    i.putExtra(Intent.EXTRA_SUBJECT, "Tip Blockchain Kasakasa")
                    i.putExtra(Intent.EXTRA_TEXT, "Send and receive crypto using usernames https://tipblockchain.io/kasakasa")
                    if (context != null) {
                        startActivity(context!!, Intent.createChooser(i, context!!.getString(R.string.share_using)), null)
                    }

                }),
/*
                MoreListItem("Sign Out", null, true),
                MoreListItem("Sign Out", R.drawable.ic_exit), */
                MoreListItem("", null, isHeader = true)
        )


        fun openUrl(url: String) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            context?.startActivity(intent)
        }

    }

}