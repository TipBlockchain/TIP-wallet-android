package io.tipblockchain.kasakasa.ui

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.ColorRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import io.tipblockchain.kasakasa.R

open class BaseActivity: AppCompatActivity() {

    protected var LOG_TAG: String? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        this.LOG_TAG = this.javaClass.name
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    protected fun showOkCancelDialog(title: String? = null, message: String, onClickListener: DialogInterface.OnClickListener? = null, onDismissListener: DialogInterface.OnDismissListener? = null) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.okay), onClickListener)
                .setNegativeButton(getString(R.string.cancel), onClickListener)
                .setOnDismissListener(onDismissListener)
                .create()
                .show()
    }

    protected fun showOkDialog(title: String? = "", message: String, onClickListener: DialogInterface.OnClickListener? = null, onDismissListener: DialogInterface.OnDismissListener? = null) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.okay), onClickListener)
                .setOnDismissListener(onDismissListener)
                .create()
                .show()
    }

    protected fun getColorFromId(@ColorRes resId: Int): Int {
        when (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            true -> return this.resources.getColor(resId, this.theme)
            false -> return ContextCompat.getColor(this, resId)
        }
    }

    protected fun showMessage(message: String) {
        var fromView: View = rootView()
        Snackbar.make(fromView, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
    }

    private fun rootView(): View {
        return window.decorView.findViewById(android.R.id.content)
    }
}