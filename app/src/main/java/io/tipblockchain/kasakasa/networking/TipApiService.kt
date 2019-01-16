package io.tipblockchain.kasakasa.networking

import android.util.Log
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import io.tipblockchain.kasakasa.app.AppConstants
import io.tipblockchain.kasakasa.config.AppProperties
import io.tipblockchain.kasakasa.data.adapter.BigIntegerAdapter
import io.tipblockchain.kasakasa.data.db.Converters
import io.tipblockchain.kasakasa.data.db.entity.Country
import io.tipblockchain.kasakasa.data.db.entity.Transaction
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.repository.AuthorizationRepository
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.data.responses.*
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.math.BigInteger

class TipApiService {

    private lateinit var tipApi: TipApi

    private constructor() {}

    companion object {
        private val baseUrl: String = AppProperties.get(AppConstants.CONFIG_API_URL)
        private val rxAdapter: RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()
        private var retrofit: Retrofit

        val instance: TipApiService = TipApiService()

        init {
            Log.i("TipApiService", "baseURL = $baseUrl")
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
                okHttpClientBuilder.addNetworkInterceptor(StethoInterceptor())
                okHttpClientBuilder.addInterceptor(authHeaderInterceptor)
                okHttpClientBuilder.addNetworkInterceptor(loggingInterceptor)
//            }

            val gson = GsonBuilder().setDateFormat(Converters.defaultDateFormat).registerTypeAdapter(BigInteger::class.java, BigIntegerAdapter()).create()
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

    fun startPhoneVerification(verification: PhoneVerificationRequest): Observable<PhoneVerificationConfirmation?> {
        return tipApi.startPhoneVerification(verification)
    }

    fun checkPhoneVerification(verification: PhoneVerificationRequest): Observable<PhoneVerificationResponse?> {
        return tipApi.checkPhoneVerification(verification)
    }

    fun checkUsername(username: String): Observable<UsernameResponse> {
        Log.d("TIPApi", "Making network request with username: $username")
        return tipApi.checkUsername(username)
    }

    fun createUser(user: User, signupToken: String, claimDemoAccount: Boolean = true): Observable<User> {
        return tipApi.createAccount(user, token = signupToken, claimDemoAccount = claimDemoAccount)
    }

    fun getMyAccount(): Observable<User?> {
        return tipApi.getMyAccount()
    }

    fun searchByUsername(username: String): Observable<UserSearchResponse> {
        return tipApi.searchByUsername(username)
    }

    fun findAccountByUsername(username: String): Observable<User?> {
        return tipApi.getAccountByUsername(username)
    }

    fun authorize(message: SecureMessage): Observable<Authorization> {
        return tipApi.authorize(message)
    }

    fun getContacts(): Observable<ContactListResponse> {
        return tipApi.getContactList()
    }

    fun uploadProfilePhoto(imageFile: File): Observable<User?> {
        var requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile)
        val multipart: MultipartBody.Part = MultipartBody.Part.createFormData("file", imageFile.name, requestBody)
        return tipApi.uploadPhoto(multipart)
    }

    fun addContact(contact: User): Observable<ContactListStringResponse> {
        val request = ContactRequest(contactId = contact.id)
        return tipApi.addContact(request)
    }

    fun addContacts(contacts: List<User>): Observable<ContactListStringResponse> {
        val contactIds = contacts.map { it.id }
        val request = ContactListRequest(contactIds = contactIds)
        return tipApi.addContacts(request)
    }

    fun removeContact(contact: User): Observable<ContactListStringResponse> {
        return tipApi.deleteContact(contact)
    }

    fun addTransaction(transaction: Transaction): Observable<Transaction?> {
        return tipApi.addTransaction(transaction)
    }

    fun getTransaction(hash: String): Observable<Transaction> {
        return tipApi.getTransaction(hash)
    }

    fun getTransactions(address: String): Observable<TransactionListResponse?> {
        return tipApi.getTransactions(address)
    }

    fun getTransactions(address: String, currency: Currency): Observable<TransactionListResponse?> {
        return tipApi.getTransactions(address = address, currency = currency.name)
    }

    fun getTransactionsByHashes(txHashList: List<String>): Observable<TransactionListResponse?> {
        return tipApi.getTransactionsByHashes(txHashList = txHashList.joinToString(","))
    }

    fun fillTransactions(txList: List<Transaction>): Observable<TransactionListResponse?> {
        return tipApi.fillTransactions(txList)
    }
}