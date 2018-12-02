package io.tipblockchain.kasakasa.networking

import io.reactivex.Observable
import io.tipblockchain.kasakasa.data.responses.EtherscanTxListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface EtherscanApi {

    /*
    https://api.etherscan.io/api?module=account&action=balance&address=0xddbd2b932c763ba5b1b7ae3b362eac3e8d40121a&tag=latest&apikey=YourApiKeyToken
     */
    fun getBalance(@Query("module") module: String,
                   @Query("action") action: String,
                   @Query("address") address: String,
                   @Query("apikey") apikey: String)

    /*
    http://api.etherscan.io/api?module=account&action=txlist&address=0xddbd2b932c763ba5b1b7ae3b362eac3e8d40121a&startblock=0&endblock=99999999&sort=asc&apikey=YourApiKeyToken
     */
    @GET(value = "/api")
    fun getEthTransactions(@Query("module") module: String,
                           @Query("action") action: String,
                           @Query("address") address: String,
                           @Query("startblock") startBlock: String,
                           @Query("endblock") endBlock: String,
                           @Query("sort") sort: String,
                           @Query("apikey") apikey: String): Observable<EtherscanTxListResponse>

    /*
    http://api.etherscan.io/api?module=account&action=tokentx&address=0x4e83362442b8d1bec281594cea3050c8eb01311c&startblock=0&endblock=999999999&sort=asc&apikey=YourApiKeyToken
     */
    @GET(value = "/api")
    fun getTipTransactions(@Query("module") module: String,
                           @Query("action") action: String,
                           @Query("address") address: String,
                           @Query("startblock") startBlock: String,
                           @Query("endblock") endBlock: String,
                           @Query("sort") sort: String,
                           @Query("apikey") apikey: String): Observable<EtherscanTxListResponse>

}