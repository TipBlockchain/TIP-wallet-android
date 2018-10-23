package io.tipblockchain.kasakasa.data.db.repository

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.app.PreferenceHelper
import io.tipblockchain.kasakasa.data.responses.Authorization
import io.tipblockchain.kasakasa.data.responses.SecureMessage
import io.tipblockchain.kasakasa.networking.TipApiService
import kotlinx.serialization.json.JSON

class AuthorizationRepository {

    companion object {
        var currentAuthorization: Authorization? = fetchCurrentAuthorization()
            set(value) {
                field = value
                if (field != null) {
                    saveCurrentAuthorization(field!!)
                }
            }


        fun getNewAuthorization(completion: (Authorization?, Throwable?)-> Unit): Observable<Authorization>? {
            val currentUser = UserRepository.currentUser
            if (currentUser != null) {
                val secureMessage = SecureMessage(message = "", address = currentUser.address, username = currentUser.username, signature = "")
                val observable = TipApiService().authorize(secureMessage)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({auth ->
                            this.currentAuthorization = auth
                            completion(auth, null)
                        }, {err ->
                            completion(null, err)
                        }, {

                        })
            } else {
                completion(null, Error("User account does not exist"))
            }
            return null
        }

        private fun fetchCurrentAuthorization(): Authorization? {
            var auth = currentAuthorization
            if (auth == null) {
                synchronized(AuthorizationRepository::class.java) {
                    val authJson = PreferenceHelper.authorization
                    if (authJson != null) {
                        auth = JSON.parse(authJson)
                    }
                }
            }
            return auth
        }

        private fun saveCurrentAuthorization(authorization: Authorization) {
            val authString = JSON.stringify(authorization)
            PreferenceHelper.authorization = authString
        }
    }
}