package io.tipblockchain.kasakasa.networking

import io.reactivex.Observable
import io.tipblockchain.kasakasa.data.db.entity.Country
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.responses.*
import okhttp3.MultipartBody
import org.web3j.abi.datatypes.Bool
import retrofit2.http.*

interface TipApi {

    // Countries
    @GET("/countries")
    fun getCountries(): Observable<List<Country>>

    @GET("/countries/{code}")
    fun getCountry(@Path("countrycode") code: String): Observable<Country>


    @POST("/phones/verificationStart")
    fun startPhoneVerification(@Body verification: PhoneVerificationRequest): Observable<PhoneVerificationConfirmation?>

    @POST("/phones/verificationCheck")
    fun checkPhoneVerification(@Body verification: PhoneVerificationRequest): Observable<PhoneVerificationResponse?>

    // Accounts
    @GET("/accounts/check")
    fun checkUsername(@Query("username") username: String, @Query("checkDemo") checkDemoAccounts: Boolean = true): Observable<UsernameResponse>

    @POST("/secure/identity")
    fun createAccount(@Body user: User, @Header("X-Signup-Token") token: String, @Header("X-Claim-Demo-Account") claimDemoAccount: Boolean = false): Observable<User>

    @GET("/accounts/my")
    fun getMyAccount(): Observable<User?>

    @GET("/accounts/profile/{username}")
    fun getAccountByUsername(@Path(value = "username") username: String): Observable<User?>

    @Multipart
    @POST("/accounts/photos")
    fun uploadPhoto(@Part image: MultipartBody.Part): Observable<User?>

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