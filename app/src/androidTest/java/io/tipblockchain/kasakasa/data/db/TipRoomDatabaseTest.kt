package io.tipblockchain.kasakasa.data.db

import android.support.test.InstrumentationRegistry
import io.tipblockchain.kasakasa.data.db.dao.TransactionDao
import io.tipblockchain.kasakasa.data.db.dao.UserDao
import io.tipblockchain.kasakasa.data.db.dao.WalletDao
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import java.math.BigInteger
import java.util.*

class TipRoomDatabaseTest {

    lateinit var userDao: UserDao
    lateinit var walletDao: WalletDao
    lateinit var transactionDao: TransactionDao

    lateinit var db: TipRoomDatabase

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getTargetContext()
        db = TipRoomDatabase.getDatabase(context)
        userDao = db.userDao()
        walletDao = db.walletDao()
        transactionDao = db.transactionDao()
    }

    @After
    fun tearDown() {
        TipRoomDatabase.destroyInstance()
    }

    @Test
    fun transactionDao() {
    }

    @Test
    fun walletDao() {
        val address = "0x0f00d"
        val filePath = "/some/path"
        val currency = "TIP"
        val value = BigInteger("1000000")

        val wallet = Wallet(address, filePath, Date(), value, currency, true, Date())
        walletDao.insert(wallet)

        val fetchedWallet = walletDao.findPrimaryWallet()
        Assert.assertNotNull(fetchedWallet)
        Assert.assertEquals(fetchedWallet, wallet)
    }

    @Test
    fun userDao() {
    }
}