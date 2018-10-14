package io.tipblockchain.kasakasa.data.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import io.tipblockchain.kasakasa.data.db.entity.Wallet

@Dao
interface WalletDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wallet: Wallet)

    @Query("SELECT * FROM wallets WHERE isPrimary = 1 LIMIT 1")
    fun findPrimaryWallet(): LiveData<Wallet?>

    @Query("SELECT * FROM wallets WHERE address = :address LIMIT 1")
    fun findWallet(address: String): LiveData<Wallet?>

    @Query("SELECT * FROM wallets")
    fun findAllWallets(): LiveData<List<Wallet>>

    @Delete
    fun  delete(wallet: Wallet)

    @Query("DELETE FROM wallets WHERE address = :address")
    fun delete(address: String)

    @Query("DELETE FROM wallets")
    fun deleteAll()
}