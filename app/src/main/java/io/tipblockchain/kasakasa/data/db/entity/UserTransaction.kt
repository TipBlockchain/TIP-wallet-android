package io.tipblockchain.kasakasa.data.db.entity

import android.arch.persistence.room.Embedded

class UserTransaction {
    @Embedded
    var user: User? = null

    @Embedded
    var transaction: Transaction? = null
}