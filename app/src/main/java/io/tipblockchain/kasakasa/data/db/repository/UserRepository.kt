package io.tipblockchain.kasakasa.data.db.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.os.AsyncTask
import com.android.example.github.AppExecutors
import com.android.example.github.api.ApiResponse
import com.android.example.github.repository.NetworkBoundResource
import com.android.example.github.vo.Resource
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.app.App
import io.tipblockchain.kasakasa.app.PreferenceHelper
import io.tipblockchain.kasakasa.data.db.TipRoomDatabase
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.dao.UserDao
import io.tipblockchain.kasakasa.data.responses.UserSearchResponse
import io.tipblockchain.kasakasa.networking.TipApiService
import kotlinx.serialization.json.JSON
import retrofit2.Response
import java.lang.Exception

typealias ContactsUpdated = (Boolean, Throwable?) -> Unit

class UserRepository {
    private var dao: UserDao
    private var apiService: TipApiService
    private var allUsers: LiveData<List<User>>
    private val appExecutors = AppExecutors()

    private var contactsFetched = false

    private constructor(application: Application) {
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

    fun findUsersBySearch(term: String): Observable<UserSearchResponse> {
        return apiService.searchByUsername(term)
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
                val liveData = apiService.getContacts()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorReturn {  ApiResponse.create(Response.success(listOf()))}
                        .toFlowable(BackpressureStrategy.LATEST)
                liveData.onErrorReturn { ApiResponse.create(Response.success(listOf()))}
                return LiveDataReactiveStreams.fromPublisher(liveData)
            }
        }.asLiveData()
    }

    fun addContact(user: User, callback: ContactsUpdated) {
        val observable = apiService.addContact(user).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe ({
            it.let {
                if (user.id in it.contacts) {
                    user.isContact = true
                    insert(user)
                }
                callback(true, null)
            }
        }, {
            callback(false, it)
        })
    }

    fun removeContact(user: User, callback: ContactsUpdated) {
        val observable = apiService.removeContact(user).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe ({
            it.let {
                if (user.id in it.contacts) {
                    user.isContact = true
                    insert(user)
                }
                callback(true, null)
            }
        }, {
            callback(false, it)
        })
    }

    fun addContacts(contacts: List<User>) {
        apiService.addContacts(contacts)
    }
    companion object {

        val instance = UserRepository(App.application())

        private class insertAsyncTask: AsyncTask<User, Int, Int> {

            private var mAsynctaskDao: UserDao

            constructor(dao: UserDao) {
                mAsynctaskDao = dao
            }

            override fun doInBackground(vararg p0: User?): Int {
                val user = p0.first()
                if (user is User) {
                    try {
                        mAsynctaskDao.insert(user)
                    } catch (e: Exception) {
                        return -1
                    }
                }
                return 0
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