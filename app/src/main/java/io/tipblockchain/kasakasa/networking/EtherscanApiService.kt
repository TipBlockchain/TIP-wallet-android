package io.tipblockchain.kasakasa.networking

import io.reactivex.Observable
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import io.tipblockchain.kasakasa.app.AppConstants
import io.tipblockchain.kasakasa.config.AppProperties
import io.tipblockchain.kasakasa.data.db.Converters
import io.tipblockchain.kasakasa.data.db.entity.Transaction
import io.tipblockchain.kasakasa.data.responses.EtherscanTxListResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class EtherscanApiService {

    private lateinit var ethApi: EtherscanApi
    private constructor()

    private var apiKey: String
    get() = AppProperties.get(AppConstants.ETHERSCAN_API_KEY)
    private set(_) {}


    companion object {
        private val baseUrl: String = AppProperties.get(AppConstants.ETHERSCAN_BASE_URL)
        private val rxAdapter: RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()
        private var retrofit: Retrofit

        val instance: EtherscanApiService = EtherscanApiService()

        init {
            val okHttpClientBuilder = OkHttpClient()
                    .newBuilder()

//            if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClientBuilder.addNetworkInterceptor(StethoInterceptor())
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
//            }

            val gson = GsonBuilder().setDateFormat(Converters.defaultDateFormat).create()
            retrofit = Retrofit.Builder()
                    .client(okHttpClientBuilder.build())
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(rxAdapter)
                    .build()
            instance.ethApi = retrofit.create(EtherscanApi::class.java)
        }
    }

    fun getEthTransactions(address: String, startBlock: String, endBlock: String): Observable<EtherscanTxListResponse> {
        return ethApi.getEthTransactions(module = "account", action = "txlist", address = address, startBlock = startBlock, endBlock = endBlock, sort = "asc", apikey = apiKey)
    }

    fun getTipTransactions(address: String, startBlock: String, endBlock: String): Observable<EtherscanTxListResponse> {
        return ethApi.getTipTransactions(module = "account", action = "tokentx", address = address, startBlock = startBlock, endBlock = endBlock, sort = "asc", apikey = apiKey)
    }
}