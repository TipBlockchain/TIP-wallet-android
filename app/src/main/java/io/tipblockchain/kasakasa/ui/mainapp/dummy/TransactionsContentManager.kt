package io.tipblockchain.kasakasa.ui.mainapp.dummy

import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.entity.Transaction
import java.math.BigInteger
import java.util.*

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object TransactionsContentManager {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<Transaction> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, Transaction> = HashMap()

    private val COUNT = 15

    init {
        createTransactions()
    }

    private fun createTransactions() {
        val users: Array<User> = ContactsContentManager.ITEMS.toTypedArray()
        var transactions: Array<Transaction> = arrayOf(
                Transaction(id = "1", hash = "0x123", from = users[5].username, to = users[11].username, currency = "TIP", value = BigInteger("122070"), time = Date(), message = "Pizza and drinks"),
                Transaction(id = "2", hash = "0x124", from = users[9].username, to = users[4].username, currency = "TIP", value = BigInteger("48202"), time = Date(), message = "Bingo")

        )

        for (item in transactions) {
            ITEMS.add(item)
            ITEM_MAP.put(item.id, item)
        }
    }

}
