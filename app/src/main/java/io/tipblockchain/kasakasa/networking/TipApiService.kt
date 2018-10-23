package io.tipblockchain.kasakasa.networking

import android.util.Log
import com.facebook.stetho.okhttp3.BuildConfig
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import io.tipblockchain.kasakasa.data.db.Converters
import io.tipblockchain.kasakasa.data.db.entity.Country
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.responses.Authorization
import io.tipblockchain.kasakasa.data.responses.SecureMessage
import io.tipblockchain.kasakasa.data.responses.UsernameResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class TipApiService {

    var tipApi: TipApi

    init {
        tipApi = retrofit.create(TipApi::class.java)
    }

    companion object {
        private val baseUrl: String = "https://0ce95d86.ngrok.io"
        private val rxAdapter: RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()
        private var retrofit: Retrofit

        init {
            val okHttpClientBuilder = OkHttpClient()
                    .newBuilder()

//            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = Level.BODY
                okHttpClientBuilder.addInterceptor(loggingInterceptor)
                okHttpClientBuilder.addInterceptor(StethoInterceptor())
//            }

            val gson = GsonBuilder().setDateFormat(Converters.defaultDateFormat).create()
            retrofit = Retrofit.Builder()
                    .client(okHttpClientBuilder.build())
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(rxAdapter)
                    .build()
        }

    }

    fun getCountries(): Observable<List<Country>> {
        return tipApi.getCountries()
    }

    fun checkUsername(username: String): Observable<UsernameResponse> {
        Log.d("TIPApi", "Making network request with username: $username")
        return tipApi.checkUsername(username)
    }

    fun createUser(user: User): Observable<User> {
        return tipApi.createAccount(user)
    }

    fun authorize(message: SecureMessage): Observable<Authorization> {
        return tipApi.authorize(message)
    }

    fun getContacts(): Observable<List<User>> {
        return tipApi.getContactList()
    }

    fun adContact(contact: User) {
        return tipApi.addContact(contact)
    }

    fun addMultipleContacts(contacts: List<User>) {
        return tipApi.addMultipleContacts(contacts)
    }

    fun deleteContact(contact: User) {
        return tipApi.deleteContact(contact)
    }
}