package io.tipblockchain.kasakasa.data.db.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import com.android.example.github.AppExecutors
import com.android.example.github.api.ApiResponse
import com.android.example.github.repository.NetworkBoundResource
import com.android.example.github.vo.Resource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.app.PreferenceHelper
import io.tipblockchain.kasakasa.data.db.TipRoomDatabase
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.dao.UserDao
import io.tipblockchain.kasakasa.data.responses.ContactListResponse
import io.tipblockchain.kasakasa.networking.TipApiService
import kotlinx.serialization.json.JSON

typealias ContactsUpdated = (Boolean, Throwable?) -> Unit

class UserRepository {
    private var dao: UserDao
    private var apiService: TipApiService
    private var allUsers: LiveData<List<User>>
    private val appExecutors = AppExecutors()

    private var contactsFetched = false

    constructor(application: Application) {
        val db = TipRoomDatabase.getDatabase(application)
        dao = db.userDao()
        apiService = TipApiService.instance
        allUsers = dao.findAllUsers()
    }

    fun insert(user: User) {
        insertAsyncTask(dao).execute(user)
    }

    fun allUsers(): LiveData<List<User>> {
        return  allUsers
    }

    fun getContactsFromDatabase(): LiveData<List<User>> {
        return dao.findContacts()
    }

    fun loadContacts(): LiveData<Resource<List<User>>> {
        return object : NetworkBoundResource<List<User>, List<User>> (appExecutors) {


            override fun saveCallResult(item: List<User>) {
                dao.insertMany(item)
            }

            override fun shouldFetch(data: List<User>?): Boolean {
                return !contactsFetched || data == null
            }

            override fun loadFromDb(): LiveData<List<User>> {
                return dao.findContacts()
            }

            override fun createCall(): LiveData<ApiResponse<List<User>>> {
                contactsFetched = true
                return apiService.getContacts()
            }
        }.asLiveData()
    }

    fun addContact(user: User, callback: ContactsUpdated) {
        val observable = apiService.addContact(user).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe ({
            it.let {
                if (user.id in it.contacts) {
                    user.isContact = true
                    dao.insert(user)
                }
                callback(true, null)
            }
        }, {
            callback(false, it)
        })
    }

    fun addContacts(contacts: List<User>) {
        apiService.addMultipleContacts(contacts)
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
                synchronized(UserRepository::class.java) {
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