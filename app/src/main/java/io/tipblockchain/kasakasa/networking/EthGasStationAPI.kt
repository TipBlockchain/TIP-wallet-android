package io.tipblockchain.kasakasa.networking

import io.reactivex.Observable
import io.tipblockchain.kasakasa.data.responses.EthGasInfo
import retrofit2.http.GET

interface EthGasStationAPI {

    @GET("/json/ethgasAPI.json")
    fun getGas(): Observable<EthGasInfo?>
}