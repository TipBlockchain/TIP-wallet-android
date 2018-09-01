package io.tipblockchain.kasakasa.config

import io.tipblockchain.kasakasa.app.App
import java.util.*

object AppProperties {
    internal val properties: Properties = Properties()
    private val configProperties = "config.properties"

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
}