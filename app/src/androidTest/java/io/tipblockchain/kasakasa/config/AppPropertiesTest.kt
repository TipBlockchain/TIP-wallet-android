package io.tipblockchain.kasakasa.config

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AppPropertiesTest {

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `loadProperties$production_sources_for_module_app`() {
        AppProperties.loadProperties()

        Assert.assertNotNull(AppProperties.properties)
    }

    @Test
    fun get() {
        Assert.assertEquals(AppProperties.get("tip_api_url"), "https://discoverapi.tipblockchain.io")
        Assert.assertEquals(AppProperties.get("ether_node_url"), "https://rinkeby.infura.io/SSWOxqisHlJoSVWYy09p")
    }
}