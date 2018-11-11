package io.tipblockchain.kasakasa.networking

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

    @GET("/accounts/profile/{username}")
    fun getAccountByUsername(@Path(value = "username") username: String): Observable<User?>

    @GET("/accounts/search")
    fun searchByUsername(@Query(value = "username") username: String): Observable<UserSearchResponse>

    @POST("/secure/authorize")
    fun authorize(@Body message: SecureMessage): Observable<Authorization>

    // Contacts
    @GET(value = "/contacts")
    fun getContactList(): Observable<ContactListResponse>

    @POST(value = "/contacts")
    fun addContact(@Body contact: ContactRequest): Observable<ContactListStringResponse>

    @POST(value = "/contacts/multiple")
    fun addContacts(@Body contactIds: ContactListRequest): Observable<ContactListStringResponse>

    @DELETE(value = "/contacts")
    fun deleteContact(contact: User): Observable<ContactListStringResponse>

}