package io.tipblockchain.kasakasa.data.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import io.tipblockchain.kasakasa.data.db.entity.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMany(users: List<User>)

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUser(id: String): LiveData<User>

    @Query("SELECT * FROM users order by name ASC")
    fun findAllUsers(): LiveData<List<User>>

    @Query("SELECT * FROM users WHERE id in (:ids)")
    fun findUsers(ids: List<String>): LiveData<List<User>>

    @Query("SELECT * FROM users WHERE isContact = 1 ORDER BY lastMessage DESC")
    fun findContacts(): LiveData<List<User>>

    @Query("SELECT * FROM users WHERE username = :username")
    fun findUserByUsername(username: String): LiveData<User>

    @Update
    fun updateUser(userToUpdate: User)

    @Delete
    fun deleteUser(user: User)
}