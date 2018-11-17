package io.tipblockchain.kasakasa.data.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import io.tipblockchain.kasakasa.data.db.entity.Transaction
import io.tipblockchain.kasakasa.data.db.entity.UserTransaction

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(transactions: List<Transaction>)

    @Query("SELECT * FROM transactions WHERE 'from' = :address OR 'to' = :address ORDER BY timestamp DESC")
    fun findTransactionsForAddress(address: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun findAllTransactions(): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions as t JOIN users as u on u.id in (t.fromTipUserId, t.toTipUserId)")
    fun findAllTransactionsWithUsers(): LiveData<List<UserTransaction>>

    @Query("SELECT * FROM transactions WHERE currency = :currency ORDER BY timestamp DESC")
    fun findTransactions(currency: String): LiveData<List<Transaction>>

    @Query("DELETE FROM transactions WHERE hash = :hash")
    fun delete(hash: String)

    @Query("DELETE FROM transactions")
    fun deleteAll()

}