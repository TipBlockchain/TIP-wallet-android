package io.tipblockchain.kasakasaprototype.ui.mainapp.dummy

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import io.tipblockchain.kasakasaprototype.R
import io.tipblockchain.kasakasaprototype.data.Contact
import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object ContactsContentManager {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<Contact> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, Contact> = HashMap()

    init {
        addItems()
    }

    private fun addItems() {
        var array: Array<Contact> = arrayOf(
                Contact(id = "1", name = "Jane Smith", alias = "@JaneSmith1989", address = "0x1048104014014", avatarUrl = "https://randomuser.me/api/portraits/med/women/65.jpg"),
                Contact(id = "2", name = "Freida Bailey", alias = "@freidaTheGreat", address = "0x1048104014014", avatarUrl = "https://randomuser.me/api/portraits/women/73.jpg"),
                Contact(id = "3", name = "Jason", alias = "@jays_fly2000", address = "0x1048104014014", avatarUrl = "https://randomuser.me/api/portraits/men/37.jpg"),
                Contact(id = "4", name = "Ryan Malinek", alias = "@malks", address = "0x1048104014014", avatarUrl = "https://randomuser.me/api/portraits/men/11.jpg"),
                Contact(id = "5", name = "Alice Burgouis", alias = "@aliceInWonderland", address = "0x1048104014014", avatarUrl = "https://randomuser.me/api/portraits/women/12.jpg"),
                Contact(id = "6", name = "Luke Harris", alias = "@harrison_ford", address = "0x1048104014014", avatarUrl = "https://randomuser.me/api/portraits/men/15.jpg"),
                Contact(id = "7", name = "Jasmine Gagnon", alias = "@ggj", address = "0x1048104014014", avatarUrl = "https://randomuser.me/api/portraits/women/17.jpg"),
                Contact(id = "8", name = "Laura Peterson", alias = "@lpFlies77", address = "0x1048104014014", avatarUrl = "https://randomuser.me/api/portraits/men/19.jpg"),
                Contact(id = "9", name = "Danielle", alias = "@JaneSmith1989", address = "0x1048104014014", avatarUrl = "https://randomuser.me/api/portraits/women/20.jpg"),
                Contact(id = "10", name = "Emily P", alias = "@milliMillsPatrick", address = "0x1048104014014", avatarUrl = "https://randomuser.me/api/portraits/women/22.jpg"),
                Contact(id = "11", name = "Volks Muller", alias = "@Vulkerzzz", address = "0x1048104014014", avatarUrl = "https://randomuser.me/api/portraits/men/22.jpg"),
                Contact(id = "12", name = "Mikkel Sorenson", alias = "@sorenz", address = "0x1048104014014", avatarUrl = "https://randomuser.me/api/portraits/men/24.jpg"),
                Contact(id = "13", name = "Rosa Parka", alias = "@freedomtobe", address = "0x1048104014014", avatarUrl = "https://randomuser.me/api/portraits/women/25.jpg"),
                Contact(id = "14", name = "Holly Martinez", alias = "@holidaymatz", address = "0x1048104014014", avatarUrl = "https://randomuser.me/api/portraits/women/29.jpg"),
                Contact(id = "15", name = "Guy Brewer", alias = "@goodguyforlife", address = "0x1048104014014", avatarUrl = "https://randomuser.me/api/portraits/men/31.jpg")
        )

        for (item in array) {
            ITEMS.add(item)
            ITEM_MAP.put(item.id, item)
        }

    }
}
