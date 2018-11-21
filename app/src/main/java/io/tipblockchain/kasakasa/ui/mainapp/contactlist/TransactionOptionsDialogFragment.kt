package io.tipblockchain.kasakasa.ui.mainapp.contactlist

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.Log
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.ui.mainapp.sendtransfer.SendTransferActivity

class TransactionOptionsDialogFragment: DialogFragment() {

    var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        username = arguments?.getString("user")
        Log.d("TX", "Got user: $username")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        var args = this.arguments
        username = args?.getString("user")

        builder.setTitle(R.string.prompt_send_or_request)
                .setItems(R.array.transaction_options) { _, which ->
                    // The 'which' argument contains the index position
                    // of the selected item
                    if (which == 0) {
                        showRequestPaymentScreen()
                    } else {
                        showSendPaymentScreen()
                    }
                }
        return builder.create()
    }


    fun showRequestPaymentScreen() {
        val intent = Intent(activity, SendTransferActivity::class.java)
        if (username != null) {
            intent.putExtra("user", username)
        }
        startActivity(intent)
    }

    fun showSendPaymentScreen() {
        val intent = Intent(activity, SendTransferActivity::class.java)
        if (username != null) {
            intent.putExtra("user", username)
        }
        startActivity(intent)
    }
}
