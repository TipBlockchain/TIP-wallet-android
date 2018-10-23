package io.tipblockchain.kasakasa.networking

import io.reactivex.Observable
import io.tipblockchain.kasakasa.data.db.entity.Country
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.responses.Authorization
import io.tipblockchain.kasakasa.data.responses.SecureMessage
import io.tipblockchain.kasakasa.data.responses.UsernameResponse
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

    @POST("/secure/authorize")
    fun authorize(@Body message: SecureMessage): Observable<Authorization>

    // Contacts
    @GET(value = "/contacts")
    fun getContactList(): Observable<List<User>>

    @POST(value = "/contacts/multiple")
    fun addMultipleContacts(newContacts: List<User>)

    @POST(value = "/contacts")
    fun addContact(contact: User)

    @DELETE(value = "/contacts")
    fun deleteContact(contact: User)

}