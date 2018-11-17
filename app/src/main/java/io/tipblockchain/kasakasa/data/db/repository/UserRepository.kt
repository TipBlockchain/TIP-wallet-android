package io.tipblockchain.kasakasa.data.db.repository

import android.app.Application
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.Observer
import android.os.AsyncTask
import android.util.Log
import com.android.example.github.AppExecutors
import com.android.example.github.api.ApiResponse
import com.android.example.github.repository.NetworkBoundResource
import com.android.example.github.vo.Resource
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.app.App
import io.tipblockchain.kasakasa.app.PreferenceHelper
import io.tipblockchain.kasakasa.data.db.TipRoomDatabase
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.dao.UserDao
import io.tipblockchain.kasakasa.data.responses.ContactListResponse
import io.tipblockchain.kasakasa.data.responses.UserSearchResponse
import io.tipblockchain.kasakasa.networking.TipApiService
import kotlinx.serialization.json.JSON
import retrofit2.Response
import java.io.File
import java.lang.Exception

typealias ContactsUpdated = (Boolean, Throwable?) -> Unit
typealias ContactsUpdatedWithResults = (List<User>?, Throwable?) -> Unit

class UserRepository {
    private var dao: UserDao
    private var apiService: TipApiService
    private var allUsers: LiveData<List<User>>
    private val LOG_TAG = javaClass.name
    private var contactsFetched = false

    private val logTag = javaClass.name
    private var fetchContactsDisposable: Disposable? = null

    private constructor(application: Application) {
        val db = TipRoomDatabase.getDatabase(application)
        dao = db.userDao()
        apiService = TipApiService.instance
        allUsers = dao.findAllUsers()
//        fetchMyAccount()
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

    fun findUserById(userId: String): LiveData<User?> {
        return dao.findUserById(userId)
    }

    fun findUsersByIds(userIds: List<String>): LiveData<List<User>> {
        return dao.findUsers(userIds)
    }

    fun findUserByUsername(username: String): LiveData<User?> {
        return dao.findUserByUsername(username)
    }

    fun fetchUsersBySearch(term: String): Observable<UserSearchResponse> {
        return apiService.searchByUsername(term)
    }

    fun fetchUserByUsername(username: String): Observable<User?> {
        return apiService.findAccountByUsername(username)
    }

    fun fetchMyAccount() {

        val disposable = apiService.getMyAccount().observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe ({
            Log.i(LOG_TAG, "My account fetched:: $it")
            if (it != null) {
                it.isContact = false
                it.isBlocked = false
                currentUser = it
            }
        }, {
            Log.e(LOG_TAG, "Error getting my account: $it")
        })
    }
    fun uploadProfilePhoto(imageFile: File): Observable<User?> {
        return apiService.uploadProfilePhoto(imageFile)
    }

    fun loadContacts(owner: LifecycleOwner, callback: ContactsUpdatedWithResults) {
        dao.findContacts().observe(owner, object : Observer<List<User>> {
            override fun onChanged(contacts: List<User>?) {

                if (!contactsFetched) {
                    fetchContactsDisposable = apiService.getContacts()
                            .subscribeOn(Schedulers.io())
                            // Have to observe on Schedulers.io() since we write to database
                            .observeOn(Schedulers.io())
                            .onErrorReturn { ContactListResponse() }
                            .subscribe ({
                                contactsFetched = true
                                val itContacts = it.contacts
                                itContacts.map { it.isContact = true }
                                dao.insertMany(itContacts)
                                AndroidSchedulers.mainThread().scheduleDirect {
                                    callback(itContacts, null)
                                }
                            }, {
                                Log.e(logTag, "Error fetching contacts: $it")
                                AndroidSchedulers.mainThread().scheduleDirect {
                                    callback(null, it)
                                }
                            })
                } else {
                    callback(contacts, null)
                }
            }
        })

    }

    fun loadContactsFromDb(): LiveData<List<User>> {
        return dao.findContacts()
    }

    fun addContact(user: User, callback: ContactsUpdated) {
        val observable = apiService.addContact(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
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

        var _currentUser: User? = null
        var currentUser: User? = fetchCurrentUser()
            set(value) {
                field = value
                _currentUser = field
                saveCurrentUser()
            }
            get() = _currentUser

        private fun fetchCurrentUser(): User? {
            if (_currentUser == null) {
                synchronized(UserRepository::class.java) {
                    val userJson = PreferenceHelper.currentUser
                    if (userJson != null) {
                        if (_currentUser == null) {
                            _currentUser = JSON.parse(userJson)
                            currentUser = _currentUser
                        }
                    }
                }
            }
            return currentUser
        }

        private fun saveCurrentUser() {
            if (_currentUser != null) {
                val userJson = JSON.stringify(_currentUser!!)
                PreferenceHelper.currentUser = userJson
            } else {
                PreferenceHelper.currentUser = null
            }
        }
    }
}