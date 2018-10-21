package io.tipblockchain.kasakasa.ui.mainapp.dummy

import io.tipblockchain.kasakasa.data.db.entity.User
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
    val ITEMS: MutableList<User> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, User> = HashMap()

    init {
        addItems()
    }

    private fun addItems() {
        var array: Array<User> = arrayOf(
                User(id = "1", name = "Jane Smith", username = "@JaneSmith1989", address = "0x1048104014014", pictureUrl = "https://randomuser.me/api/portraits/med/women/65.jpg"),
                User(id = "2", name = "Freida Bailey", username = "@freidaTheGreat", address = "0x1048104014014", pictureUrl = "https://randomuser.me/api/portraits/women/73.jpg"),
                User(id = "3", name = "Jason", username = "@jays_fly2000", address = "0x1048104014014", pictureUrl = "https://randomuser.me/api/portraits/men/37.jpg"),
                User(id = "4", name = "Ryan Malinek", username = "@malks", address = "0x1048104014014", pictureUrl = "https://randomuser.me/api/portraits/men/11.jpg"),
                User(id = "5", name = "Alice Burgouis", username = "@aliceInWonderland", address = "0x1048104014014", pictureUrl = "https://randomuser.me/api/portraits/women/12.jpg"),
                User(id = "6", name = "Luke Harris", username = "@harrison_ford", address = "0x1048104014014", pictureUrl = "https://randomuser.me/api/portraits/men/15.jpg"),
                User(id = "7", name = "Jasmine Gagnon", username = "@ggj", address = "0x1048104014014", pictureUrl = "https://randomuser.me/api/portraits/women/17.jpg"),
                User(id = "8", name = "Laura Peterson", username = "@lpFlies77", address = "0x1048104014014", pictureUrl = "https://randomuser.me/api/portraits/men/19.jpg"),
                User(id = "9", name = "Danielle", username = "@JaneSmith1989", address = "0x1048104014014", pictureUrl = "https://randomuser.me/api/portraits/women/20.jpg"),
                User(id = "10", name = "Emily P", username = "@milliMillsPatrick", address = "0x1048104014014", pictureUrl = "https://randomuser.me/api/portraits/women/22.jpg"),
                User(id = "11", name = "Volks Muller", username = "@Vulkerzzz", address = "0x1048104014014", pictureUrl = "https://randomuser.me/api/portraits/men/22.jpg"),
                User(id = "12", name = "Mikkel Sorenson", username = "@sorenz", address = "0x1048104014014", pictureUrl = "https://randomuser.me/api/portraits/men/24.jpg"),
                User(id = "13", name = "Rosa Parka", username = "@freedomtobe", address = "0x1048104014014", pictureUrl = "https://randomuser.me/api/portraits/women/25.jpg"),
                User(id = "14", name = "Holly Martinez", username = "@holidaymatz", address = "0x1048104014014", pictureUrl = "https://randomuser.me/api/portraits/women/29.jpg"),
                User(id = "15", name = "Guy Brewer", username = "@goodguyforlife", address = "0x1048104014014", pictureUrl = "https://randomuser.me/api/portraits/men/31.jpg")
        )

        for (item in array) {
            ITEMS.add(item)
            ITEM_MAP.put(item.id, item)
        }

    }
}
