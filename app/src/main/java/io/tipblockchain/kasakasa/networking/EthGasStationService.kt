package io.tipblockchain.kasakasa.networking

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import io.tipblockchain.kasakasa.data.db.Converters
import io.tipblockchain.kasakasa.data.responses.EthGasInfo
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class EthGasStationService {

    private lateinit var gasApi: EthGasStationAPI

    companion object {
        private val baseUrl: String = "https://ethgasstation.info"
        private val rxAdapter: RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()
        private var retrofit: Retrofit

        val instance: EthGasStationService = EthGasStationService()

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
            instance.gasApi = retrofit.create(EthGasStationAPI::class.java)
        }
    }

    fun getGasInfo(): Observable<EthGasInfo?> {
        return gasApi.getGas()
    }

}