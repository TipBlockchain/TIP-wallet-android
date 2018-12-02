package io.tipblockchain.kasakasa.data.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.internal.operators.flowable.FlowableSingle
import io.tipblockchain.kasakasa.data.db.entity.Wallet

@Dao
interface WalletDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wallet: Wallet)

    @Query("SELECT * FROM wallets WHERE isPrimary = 1 LIMIT 1")
    fun findPrimaryWallet(): LiveData<Wallet?>

    @Query("SELECT * FROM wallets WHERE address = :address LIMIT 1")
    fun findWallet(address: String): LiveData<Wallet?>

    @Query(value = "SELECT * from wallets WHERE currency = :currency")
    fun findWalletForCurrency(currency: String): LiveData<Wallet?>

    @Query(value = "SELECT * from wallets WHERE address = :address AND currency = :currency")
    fun findWalletForAddressAndCurrency(address: String, currency: String): Single<Wallet?>

    @Query("SELECT * FROM wallets")
    fun findAllWallets(): LiveData<List<Wallet>>

    @Update()
    fun update(wallet: Wallet)

    @Delete
    fun  delete(wallet: Wallet)

    @Query("DELETE FROM wallets WHERE address = :address")
    fun delete(address: String)

    @Query("DELETE FROM wallets")
    fun deleteAll()
}