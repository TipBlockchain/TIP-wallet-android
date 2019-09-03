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
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import io.tipblockchain.kasakasa.R
import android.widget.FrameLayout

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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun showBackButton(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

    }

    fun showOkCancelDialog(title: String? = null, message: String, onClickListener: DialogInterface.OnClickListener? = null, onDismissListener: DialogInterface.OnDismissListener? = null) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.okay), onClickListener)
                .setNegativeButton(getString(R.string.cancel), onClickListener)
                .setOnDismissListener(onDismissListener)
                .create()
                .show()
    }

    fun showOkDialog(title: String? = "", message: String, onClickListener: DialogInterface.OnClickListener? = null, onDismissListener: DialogInterface.OnDismissListener? = null) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.okay), onClickListener)
                .setOnDismissListener(onDismissListener)
                .create()
                .show()
    }

    open fun showEnterPasswordDialog(onCompletion: (String) -> Unit, onCancel: (() -> Unit)? = null) {
        val view = layoutInflater.inflate(R.layout.dialog_confirm_password, null)
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle(getString(R.string.enter_password))
        alertDialog.setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_secure))
        alertDialog.setCancelable(false)

        val passwordView =  view.findViewById(R.id.passwordTv) as EditText

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.okay)) { _, _ ->
            val password = passwordView.text.toString()
            onCompletion(password)
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
            if (onCancel != null) {
                onCancel()
            }
        }

        alertDialog.setView(view)
        alertDialog.show()
    }

    open fun showEnterTextDialog(title: String, message: String, initialText: String = "", onEnter: ((text: String) -> Unit)? = null, onCancel: (() -> Unit)? = null) {
        val alert = AlertDialog.Builder(this)

        val edittext = EditText(this)
        val layout = FrameLayout(this)

//set padding in parent layout
        layout.setPaddingRelative(60,15,60,0)

        alert.setTitle(title)

        layout.addView(edittext)

        alert.setView(layout)
        alert.setTitle(title)
        alert.setMessage(message)

        edittext.setText(initialText)

        alert.setPositiveButton(getString(R.string.save)) { dialog, whichButton ->
            //What ever you want to do with the value
            //OR
            if (onEnter != null) {
                val text = edittext.text.toString()
                onEnter(text)
            }
        }

        alert.setNegativeButton(getString(R.string.cancel)) { dialog, whichButton ->
            // what ever you want to do with No option.
            if (onCancel != null) {
                onCancel()
            }
        }

        alert.show()
    }

    protected fun getColorFromId(@ColorRes resId: Int): Int {
        when (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            true -> return this.resources.getColor(resId, this.theme)
            false -> return ContextCompat.getColor(this, resId)
        }
    }

    open fun showMessage(message: String) {
        var fromView: View = rootView()
        Snackbar.make(fromView, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
    }

    private fun rootView(): View {
        return window.decorView.findViewById(android.R.id.content)
    }
}