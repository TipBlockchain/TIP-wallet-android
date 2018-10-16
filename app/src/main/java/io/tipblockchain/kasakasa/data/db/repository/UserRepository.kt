package io.tipblockchain.kasakasa.data.db.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import io.tipblockchain.kasakasa.app.PreferenceHelper
import io.tipblockchain.kasakasa.data.db.TipRoomDatabase
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.dao.UserDao
import kotlinx.serialization.json.JSON

class UserRepository {
    private var dao: UserDao
    private var allUsers: LiveData<List<User>>

    constructor(application: Application) {
        val db = TipRoomDatabase.getDatabase(application)
        dao = db.userDao()
        allUsers = dao.findAllUsers()
    }

    fun insert(user: User) {
        insertAsyncTask(dao).execute(user)
    }

    fun allUsers(): LiveData<List<User>> {
        return  allUsers
    }



    companion object {

        private class insertAsyncTask: AsyncTask<User, Int, Int> {

            private var mAsynctaskDao: UserDao

            constructor(dao: UserDao) {
                mAsynctaskDao = dao
            }

            override fun doInBackground(vararg p0: User?): Int {
                val user = p0.first()
                if (user is User) {
                    mAsynctaskDao.insert(user)
                    return 0
                }
                return -1
            }
        }

        var currentUser: User? = fetchCurrentUser()
            set(value) {
                field = value
                saveCurrentUser()
            }

        private fun fetchCurrentUser(): User? {
            if (currentUser == null) {
                synchronized(User::class.java) {
                    val userJson = PreferenceHelper.currentUser
                    if (userJson != null) {
                        if (currentUser == null) {
                            currentUser = JSON.parse(userJson)
                        }
                    }
                }
            }
            return currentUser
        }

        private fun saveCurrentUser() {
            if (currentUser != null) {
                val userJson = JSON.stringify(currentUser!!)
                PreferenceHelper.currentUser = userJson
            } else {
                PreferenceHelper.currentUser = null
            }
        }
    }
}