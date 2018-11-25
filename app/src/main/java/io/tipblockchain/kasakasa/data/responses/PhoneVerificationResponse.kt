package io.tipblockchain.kasakasa.data.responses

import io.tipblockchain.kasakasa.data.db.entity.User

data class PhoneVerificationResponse (var account: User?, var demoAccount: User?, var pendingSignup: PendingSignup?, var authorization: Authorization?)

