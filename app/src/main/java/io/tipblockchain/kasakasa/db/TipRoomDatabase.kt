package io.tipblockchain.kasakasa.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import io.tipblockchain.kasakasa.db.dao.TransactionDao
import io.tipblockchain.kasakasa.db.dao.UserDao
import io.tipblockchain.kasakasa.db.dao.WalletDao
import io.tipblockchain.kasakasa.db.entity.Transaction
import io.tipblockchain.kasakasa.db.entity.User
import io.tipblockchain.kasakasa.db.entity.Wallet


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