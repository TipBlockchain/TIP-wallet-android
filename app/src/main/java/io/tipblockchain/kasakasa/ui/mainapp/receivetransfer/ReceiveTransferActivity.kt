package io.tipblockchain.kasakasa.ui.mainapp.receivetransfer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.ShareActionProvider
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.ui.BaseActivity

import kotlinx.android.synthetic.main.activity_receive_transfer.*
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.squareup.picasso.Picasso
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.repository.UserRepository
import kotlinx.android.synthetic.main.content_receive_transfer.*


class ReceiveTransferActivity : BaseActivity(), ReceiveTransfer.View {

    var presenter: ReceiveTransfer.Presenter? = null
    private var mShareActionProvider: ShareActionProvider? = null
    private var address: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive_transfer)
        presenter = ReceiveTransferPresenter()
        presenter!!.attach(this)

        copyBtn.setOnClickListener { copyAddress() }
    }

    override fun onResume() {
        super.onResume()
        presenter?.loadWallet()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detach()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate menu resource file.
        menuInflater.inflate(R.menu.menu_share, menu)

        // Locate MenuItem with ShareActionProvider
        menu.findItem(R.id.menu_item_share).also { menuItem ->
            // Fetch and store ShareActionProvider
            mShareActionProvider = MenuItemCompat.getActionProvider(menuItem) as? ShareActionProvider
            mShareActionProvider?.setShareIntent(getShareIntent())
        }

        // Return true to display menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(LOG_TAG, "Options item selected: $item")
        val currentUser = UserRepository.currentUser
        if (item.itemId == R.id.menu_item_share && currentUser != null) {
            shareAccountInfo(currentUser)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showWallet(address: String) {
        val user = UserRepository.currentUser
        usernameTv.setText(user?.username)
        if (user?.originalPhotoUrl != null) {
            Picasso.get().load(user.originalPhotoUrl).into(displayImageView)
        } else {
            Picasso.get().load(R.drawable.avatar_placeholder_small).into(displayImageView)
        }
        this.address = address
        try {
            val barcodeEncoder = BarcodeEncoder()
            val size = qrCodeImageView.height
            val bitmap = barcodeEncoder.encodeBitmap(address, BarcodeFormat.QR_CODE, size, size)
            qrCodeImageView.setImageBitmap(bitmap)
            addressTv.setText(address)
        } catch (e: Exception) {
            showMessage("Error loading wallets: ${e.localizedMessage}")
        }
    }

    private fun getShareIntent(): Intent {
        val user = UserRepository.currentUser
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT,getString(R.string.share_address, user?.username, user?.address))
            type = "text/plain"
        }
        return sendIntent
    }
    private fun shareAccountInfo(user: User) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT,getString(R.string.share_address, user.username, user.address))
            type = "text/plain"
        }
        setShareIntent(sendIntent)
    }

    private fun setShareIntent(shareIntent: Intent) {
        mShareActionProvider?.setShareIntent(shareIntent)
    }

    override fun copyAddress() {
        if (address != null) {
            val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData: ClipData = ClipData.newPlainText("My TIP and ETH address", address)
            clipboard.primaryClip = clipData

            showMessage(getString(R.string.address_copied))
        } else {
            showMessage(getString(R.string.error_copying_address))
        }
    }
}
