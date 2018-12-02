package io.tipblockchain.kasakasa.data.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import android.os.AsyncTask
import io.tipblockchain.kasakasa.data.db.dao.TransactionDao
import io.tipblockchain.kasakasa.data.db.dao.UserDao
import io.tipblockchain.kasakasa.data.db.dao.WalletDao
import io.tipblockchain.kasakasa.data.db.entity.Transaction
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import android.arch.persistence.room.migration.Migration




@Database(entities = arrayOf(Transaction::class, User::class, Wallet::class), version = 1)
@TypeConverters(Converters::class)
abstract class TipRoomDatabase: RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun walletDao(): WalletDao
    abstract fun userDao(): UserDao

    companion object {
        private var INSTANCE: TipRoomDatabase? = null

        private const val dbName = "tip_database"

        fun getDatabase(context: Context): TipRoomDatabase {
            if (INSTANCE == null) {
                synchronized(TipRoomDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext, TipRoomDatabase::class.java, dbName)
                                .addCallback(sRoomDatabaseCallback)
                                .build()
                    }
                }
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            if (INSTANCE != null) {
                INSTANCE!!.close()
                INSTANCE = null
            }
        }


        private val sRoomDatabaseCallback = object : RoomDatabase.Callback() {

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                PopulateDbAsync(INSTANCE!!).execute()
            }
        }


        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("INSERT INTO wallets (address, filePath, created, lastSynced, balance, isPrimary) " +
                        "   SELECT address, filePath, created, lastSynced, balance, isPrimary FROM wallet2;")
            }
        }
    }


    private class PopulateDbAsync internal constructor(db: TipRoomDatabase) : AsyncTask<Void, Void, Void>() {

        private val txDao: TransactionDao

        init {
            txDao = db.transactionDao()
        }

        override fun doInBackground(vararg params: Void): Void? {
            return null
        }
    }
}