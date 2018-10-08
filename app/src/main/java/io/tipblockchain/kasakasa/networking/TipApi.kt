package io.tipblockchain.kasakasa.networking

import io.reactivex.Observable
import io.tipblockchain.kasakasa.data.db.entity.Country
import io.tipblockchain.kasakasa.data.responses.UsernameResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TipApi {

    // Countries
    @GET("/countries")
    fun getCountries(): Observable<List<Country>>

    @GET("/countries/{code}")
    fun getCountry(@Path("countrycode") code: String): Observable<Country>


    // Accounts
    @GET("/accounts/check")
    fun checkUsername(@Query("username") username: String): Observable<UsernameResponse>
}