package io.tipblockchain.kasakasa.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.tipblockchain.kasakasa.db.entity.Transaction

@Dao
interface TransactionDao {

    @Insert
    fun insert(transaction: Transaction)

    @Insert
    fun insertAll(transactions: List<Transaction>)

    @Query("SELECT * FROM transactions ORDER BY time DESC")
    fun findAllTransactions(): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE currency = :currency ORDER BY time DESC")
    fun findTransactions(currency: String): LiveData<List<Transaction>>

    @Query("DELETE FROM transactions WHERE id = :id")
    fun delete(id: String)

    @Query("DELETE FROM transactions")
    fun deleteAll()

}