package io.tipblockchain.kasakasa.db.repository

import android.arch.lifecycle.LiveData
import android.content.Context
import android.os.AsyncTask
import io.tipblockchain.kasakasa.db.TipRoomDatabase
import io.tipblockchain.kasakasa.db.entity.User
import io.tipblockchain.kasakasa.db.dao.UserDao

class UserRepository {
    private var dao: UserDao
    private var allUsers: LiveData<List<User>>

    constructor(context: Context) {
        val db = TipRoomDatabase.getDatabase(context)
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

    }
}