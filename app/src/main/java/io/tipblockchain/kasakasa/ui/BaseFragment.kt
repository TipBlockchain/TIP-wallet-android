package io.tipblockchain.kasakasa.ui

import android.content.DialogInterface
import android.os.Build
import android.support.annotation.ColorRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import io.tipblockchain.kasakasa.R

open class BaseFragment: Fragment() {

    protected fun showOkCancelDialog(title: String? = null, message: String, onClickListener: DialogInterface.OnClickListener? = null, onDismissListener: DialogInterface.OnDismissListener? = null) {
        AlertDialog.Builder(context!!)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.okay), onClickListener)
                .setNegativeButton(getString(R.string.cancel), onClickListener)
                .setOnDismissListener(onDismissListener)
                .create()
                .show()
    }

    protected fun showOkDialog(message: String, onClickListener: DialogInterface.OnClickListener? = null, onDismissListener: DialogInterface.OnDismissListener? = null) {
        AlertDialog.Builder(context!!)
                .setMessage(message)
                .setPositiveButton(getString(R.string.okay), onClickListener)
                .setOnDismissListener(onDismissListener)
                .create()
                .show()
    }

    protected fun getColorFromId(@ColorRes resId: Int): Int {
        when (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            true -> return this.resources.getColor(resId, context!!.theme)
            false -> return ContextCompat.getColor(context!!, resId)
        }
    }

//    protected fun showMessage(message: String) {
//        var fromView: View = rootView()
//        Snackbar.make(fromView, message, Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//    }
}