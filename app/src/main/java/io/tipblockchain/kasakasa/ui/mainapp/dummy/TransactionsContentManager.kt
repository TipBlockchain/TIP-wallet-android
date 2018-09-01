package io.tipblockchain.kasakasa.ui.mainapp.dummy

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.data.Contact
import io.tipblockchain.kasakasa.data.Transaction
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
        val contacts: Array<Contact> = ContactsContentManager.ITEMS.toTypedArray()
        var transactions: Array<Transaction> = arrayOf(
                Transaction(id = "1", hash = "0x123", from = contacts[5], to = contacts[11], amount = 1220.70f, time = Date(),  message = "Pizza and drinks"),
                Transaction(id = "2", hash = "0x124", from = contacts[9], to = contacts[4], amount = 100.00f, time = Date(),  message = "Bingo")

        )

        for (item in transactions) {
            ITEMS.add(item)
            ITEM_MAP.put(item.id, item)
        }
    }

}
