package io.tipblockchain.kasakasa.config

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.app.App
import io.tipblockchain.kasakasa.networking.TipApiService
import java.util.*

object AppProperties {
    internal val properties: Properties = Properties()
    private val configProperties = "config.properties"
    private val LOG_TAG = javaClass.name

    init {
        loadProperties()
    }

    internal fun loadProperties() {
        val assets = App.applicationContext().assets
        val inputStream = assets.open(configProperties)
        properties.load(inputStream)
    }

    fun get(value: String) : String {
        return properties.getProperty(value)
    }

    internal fun fetchProperties(completion: (success: Boolean) -> Unit) {
        Log.d(LOG_TAG, "fetching properties")
        val tipApi = TipApiService.instance
        val disposable = tipApi.getAppConfig().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe( { json ->
                    Log.d(LOG_TAG, "json fetched $json")
                    for (entry in json.entrySet()) {
                        if (entry.value.isJsonPrimitive) {
                            properties[entry.key] = entry.value.asString
                        }
                    }
                    Log.d(LOG_TAG, "All properties are now ${properties.stringPropertyNames()}")
                    completion(true)

        }, {err ->
                    Log.e(LOG_TAG, "Failed to fetch properties: $err -> ${err.localizedMessage}\n -> Cause -> ${err.cause}")
                    completion(false)

                })
    }
}