package io.tipblockchain.kasakasa.db.entity

import org.junit.After
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.math.BigInteger
import java.util.*

class WalletTest {

    lateinit var wallet: Wallet

    val address = "0x0f00d"
    val filePath = "/some/path"
    val currency = "TIP"
    val value = BigInteger("1000000")

    @Before
    fun setUp() {
        wallet = Wallet(address, filePath, Date(), value, currency, true, Date())
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testProperties() {
        Assert.assertEquals(wallet.address, address)
        Assert.assertEquals(wallet.balance, value)
        Assert.assertEquals(wallet.currency, currency)
        Assert.assertEquals(wallet.isPrimary, true)
    }

}