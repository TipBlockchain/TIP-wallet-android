package io.tipblockchain.kasakasa.ui.mainapp.contactlist

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.ui.mainapp.sendtransfer.SendTransferActivity

class TransactionOptionsDialogFragment: DialogFragment() {

    override  fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
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
        intent.putExtra("keyIdentifier", "value")
        startActivity(intent)
    }

    fun showSendPaymentScreen() {
        val intent = Intent(activity, SendTransferActivity::class.java)
        intent.putExtra("keyIdentifier", "value")
        startActivity(intent)
    }
}
