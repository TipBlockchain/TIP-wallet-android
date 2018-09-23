package io.tipblockchain.kasakasa.ui.onboarding.password

import android.arch.lifecycle.ViewModel
import android.text.TextUtils
import android.text.Editable
import android.databinding.adapters.TextViewBindingAdapter.setPassword
import android.text.TextWatcher
import android.util.Log


class ChoosePasswordViewModel(var password: String = "", var confirmedPassword: String = "") : ViewModel() {

    fun passwordIsEmpty(): Boolean {
        return TextUtils.isEmpty(password)
    }

    fun isPasswordValid(): Boolean {
        if (TextUtils.isEmpty(password)) {
            return false
        }

        return password.length >= 8
    }

    val passwordsMatch: () -> Boolean = {
        password.equals(confirmedPassword)
    }

    fun getPasswordTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                password = s.toString()
                Log.d("GIT", "New text = ${s}")
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        }
    }
}