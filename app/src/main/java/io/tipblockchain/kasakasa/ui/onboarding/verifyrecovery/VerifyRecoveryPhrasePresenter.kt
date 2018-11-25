package io.tipblockchain.kasakasa.ui.onboarding.verifyrecovery

import java.util.*

class VerifyRecoveryPhrasePresenter: VerifyRecoveryPhrase.Presenter {
    private lateinit var originalPhrase: String
    private val underscore = "______"

    override var view: VerifyRecoveryPhrase.View? = null

    override fun setRecoveryPhrase(phrase: String) {
        originalPhrase = phrase
    }

    override fun removeWords() {
        val wordlist: MutableList<String> = originalPhrase.split(" ").toMutableList()
        val random = Random()
        var upper = wordlist.size/2
        var lower = 1
        val firstIndex = random.nextInt(upper - lower) + lower
        wordlist[firstIndex] = underscore
        lower =  upper
        upper = wordlist.size
        val secondIndex = random.nextInt(upper - lower) + lower
        wordlist[secondIndex] =  underscore

        view?.onWordsRemoved(wordlist.joinToString(" "), firstIndex, secondIndex)

    }

    override fun verifyRecoveryPhrase(phrase: String, word1: String, word2: String) {
        val wordlist = phrase.split(" ").toMutableList()
        val firstIndex = wordlist.indexOfFirst {
            it == underscore
        }
        val lastIndex = wordlist.indexOfLast {
            it == underscore
        }

        wordlist[firstIndex] = word1
        wordlist[lastIndex] = word2

        val phraseToVerify = wordlist.joinToString(" ")
        if (phraseToVerify == originalPhrase) {
            view?.onPhraseVerified()
        } else {
            view?.onVerificationError()
        }
    }
}