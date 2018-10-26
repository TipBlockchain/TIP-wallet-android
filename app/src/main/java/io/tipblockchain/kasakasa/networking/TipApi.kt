package io.tipblockchain.kasakasa.networking

import android.arch.lifecycle.LiveData
import com.android.example.github.api.ApiResponse
import io.reactivex.Observable
import io.tipblockchain.kasakasa.data.db.entity.Country
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.responses.*
import retrofit2.http.*

interface TipApi {

    // Countries
    @GET("/countries")
    fun getCountries(): Observable<List<Country>>

    @GET("/countries/{code}")
    fun getCountry(@Path("countrycode") code: String): Observable<Country>


    // Accounts
    @GET("/accounts/check")
    fun checkUsername(@Query("username") username: String): Observable<UsernameResponse>

    @POST("/secure/identity")
    fun createAccount(@Body user: User): Observable<User>

    @GET("/accounts/search")
    fun searchByUsername(@Query(value = "username") username: String): Observable<UserSearchResponse>

    @POST("/secure/authorize")
    fun authorize(@Body message: SecureMessage): Observable<Authorization>

    // Contacts
    @GET(value = "/contacts")
    fun getContactList(): LiveData<ApiResponse<List<User>>>

    @POST(value = "/contacts/multiple")
    fun addMultipleContacts(newContacts: List<User>): Observable<ContactListResponse>

    @POST(value = "/contacts")
    fun addContact(contact: User): Observable<ContactListResponse>

    @DELETE(value = "/contacts")
    fun deleteContact(contact: User): Observable<ContactListResponse>

}