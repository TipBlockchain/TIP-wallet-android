package io.tipblockchain.kasakasa.ui.onboarding.recovery

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.crypto.MnemonicUtils
import io.tipblockchain.kasakasa.crypto.WalletUtils

class RecoveryPhrasePresenter: RecoveryPhrase.Presenter {

    override var view: RecoveryPhrase.View? = null

    override fun getNewMnemonic() {
//        Schedulers.io().scheduleDirect {
            try {
                var mnemonic: String

                do {
                    mnemonic = WalletUtils.generateBip39Mnemonic()
                } while (!MnemonicUtils.isValidSeedPhrase(mnemonic))

//                AndroidSchedulers.mainThread().scheduleDirect {
                    view?.onMnemonicCreated(mnemonic)
//                }
            } catch (err: Throwable) {
//                AndroidSchedulers.mainThread().scheduleDirect {
                    view?.onError(err)
//                }
            }
//        }
    }

}