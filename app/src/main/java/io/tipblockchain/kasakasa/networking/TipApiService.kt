package io.tipblockchain.kasakasa.networking

import android.util.Log
import io.reactivex.Observable
import io.tipblockchain.kasakasa.data.db.entity.Country
import io.tipblockchain.kasakasa.data.responses.UsernameResponse
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class TipApiService {

    var tipApi: TipApi

    init {
        tipApi = retrofit.create(TipApi::class.java)
    }

    companion object {
        val baseUrl: String = "http://fcf2db90.ngrok.io"
        var rxAdapter: RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()
        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build()


    }

    fun getCountries(): Observable<List<Country>> {
        return tipApi.getCountries()
    }

    fun checkUsername(username: String): Observable<UsernameResponse> {
        Log.d("TIPApi", "Making network request with username: $username")
        return tipApi.checkUsername(username)
    }
}