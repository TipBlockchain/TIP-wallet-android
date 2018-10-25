package io.tipblockchain.kasakasa.networking

import android.arch.lifecycle.LiveData
import android.util.Log
import com.android.example.github.api.ApiResponse
import com.facebook.stetho.okhttp3.BuildConfig
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.android.gms.common.api.Api
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import io.tipblockchain.kasakasa.data.db.Converters
import io.tipblockchain.kasakasa.data.db.entity.Country
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.repository.AuthorizationRepository
import io.tipblockchain.kasakasa.data.responses.Authorization
import io.tipblockchain.kasakasa.data.responses.ContactListResponse
import io.tipblockchain.kasakasa.data.responses.SecureMessage
import io.tipblockchain.kasakasa.data.responses.UsernameResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class TipApiService {

    private lateinit var tipApi: TipApi

    private constructor() {}


    companion object {
        private val baseUrl: String = "https://0ce95d86.ngrok.io"
        private val rxAdapter: RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()
        private var retrofit: Retrofit

        val instance: TipApiService = TipApiService()

        init {
            val okHttpClientBuilder = OkHttpClient()
                    .newBuilder()

            val authHeaderInterceptor = Interceptor {chain ->
                val builder = chain.request().newBuilder()
                if (AuthorizationRepository.currentAuthorization != null) {
                    builder.addHeader("Authorization", "Bearer ${AuthorizationRepository.currentAuthorization!!.token}")
                }
                chain.proceed(builder.build())
            }

//            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = Level.BODY
                okHttpClientBuilder.addInterceptor(loggingInterceptor)
                okHttpClientBuilder.addInterceptor(StethoInterceptor())
                okHttpClientBuilder.addInterceptor(authHeaderInterceptor)
//            }

            val gson = GsonBuilder().setDateFormat(Converters.defaultDateFormat).create()
            retrofit = Retrofit.Builder()
                    .client(okHttpClientBuilder.build())
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(rxAdapter)
                    .build()
            instance.tipApi = retrofit.create(TipApi::class.java)
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

    fun getContacts(): LiveData<ApiResponse<List<User>>> {
        return tipApi.getContactList()
    }

    fun addContact(contact: User): Observable<ContactListResponse> {
        return tipApi.addContact(contact)
    }

    fun addMultipleContacts(contacts: List<User>): Observable<ContactListResponse> {
        return tipApi.addMultipleContacts(contacts)
    }

    fun deleteContact(contact: User): Observable<ContactListResponse> {
        return tipApi.deleteContact(contact)
    }
}