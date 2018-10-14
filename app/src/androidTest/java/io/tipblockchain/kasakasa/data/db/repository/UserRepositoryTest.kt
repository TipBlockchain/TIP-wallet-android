package io.tipblockchain.kasakasa.data.db.repository

import io.tipblockchain.kasakasa.app.App
import io.tipblockchain.kasakasa.data.db.entity.User
import org.junit.After
import org.junit.Assert
import org.junit.Before

import org.junit.Test

class UserRepositoryTest {

    lateinit var repository: UserRepository
    @Before
    fun setUp() {
        repository = UserRepository(App.application())
    }

    @After
    fun tearDown() {
        UserRepository.currentUser = null
    }

    @Test
    fun saveCurrentUser() {
        val user = User("randomId", "John Doe", "JohnDoe", "0x123")
        UserRepository.currentUser = user

        var fetchedUser = UserRepository.currentUser
        Assert.assertNotNull(fetchedUser)
        Assert.assertEquals(user.name, fetchedUser!!.name)
        Assert.assertEquals(user.address, fetchedUser!!.address)
        Assert.assertEquals(user.id, fetchedUser!!.id)
        Assert.assertEquals(user.username, fetchedUser!!.username)
    }
}