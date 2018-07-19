package io.tipblockchain.kasakasaprototype.data

import java.util.*

class Transaction(val id: String, val hash: String, val from: Contact, val to: Contact, val amount: Float, val time: Date, val message: String) {

}