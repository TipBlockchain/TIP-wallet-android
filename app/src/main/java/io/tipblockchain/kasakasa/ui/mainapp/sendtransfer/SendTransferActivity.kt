package io.tipblockchain.kasakasa.ui.mainapp.sendtransfer

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import com.google.zxing.client.android.Intents
import com.google.zxing.integration.android.IntentIntegrator
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.app.AppConstants
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.data.responses.PendingTransaction
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.mainapp.confirmtransfer.ConfirmTransferActivity
import io.tipblockchain.kasakasa.utils.TextUtils
import kotlinx.android.synthetic.main.activity_send_transfer.*
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class SendTransferActivity : BaseActivity(), SendTransfer.View, AdapterView.OnItemSelectedListener {
    private val REQUEST_READ_CONTACTS = 0
    var presenter: SendTransferPresenter? = null
    var adapter: UserFilterAdapter? = null
    val scannerRequestCode = 99
    private val defaultGasPriceInGwei = 15
    private var currentGasPriceInGwei = defaultGasPriceInGwei
    private var transactionFeeInEth: BigDecimal? = null
    private var selectedWallet: Wallet? = null
    private var wallets: List<Wallet> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_transfer)

        // Set up the login form.
        populateAutoComplete()
        adapter = UserFilterAdapter(this, listOf())
        recepientTv.setAdapter(adapter)

        recepientTv.threshold = 1

        nextButton.setOnClickListener { nextButtonClicked() }
        scanButton.setOnClickListener { showQRCodeScanner() }
        var username = intent.getStringExtra(AppConstants.TRANSACTION_RECIPIENT)
        if (username  != null) {
            recepientTv.setText(username)
        }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, value: Int, b: Boolean) {
                // Display the current progress of SeekBar
                setGasPrice(gasPrice = value)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
            }
        })
        setupPresenter()
        setGasPrice(defaultGasPriceInGwei)
    }

    override fun onResume() {
        super.onResume()
//        spinner.setSelection(0)
    }

    private fun setupPresenter() {
        presenter = SendTransferPresenter()

        presenter?.attach(this)
        presenter?.loadContactList()
        presenter?.loadWallets()
    }

    override fun onDestroy() {
        presenter?.detach()
        super.onDestroy()
    }

    override fun showWallets(wallets: List<Wallet>) {
        this.wallets = wallets
        this.setupSpinner(wallets)
    }

    private fun populateAutoComplete() {
        if (!mayRequestContacts()) {
            return
        }
    }

    private fun setupSpinner(wallets: List<Wallet>) {
        Log.d(LOG_TAG, "Setting up spinner")
        val walletNames = wallets.map {
            it.displayName()
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, walletNames)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this

        val wallet = intent.getSerializableExtra(AppConstants.EXTRA_CURRENT_WALLET) as? Wallet
        this.selectedWallet = wallet
        Log.d(LOG_TAG, "Selected wallet = $selectedWallet")
        if (wallet != null && !wallets.isEmpty()) {
            for (i in 0 .. wallets.size) {
                if (wallets[i].currency == wallet.currency && wallets[i].address == wallet.address) {
                    spinner.setSelection(i)
                    break
                }
            }
        } else {
            Log.d(LOG_TAG, "Currency is null")
        }
    }

    private fun setGasPrice(gasPrice: Int) {
        currentGasPriceInGwei = gasPrice
        if (currentGasPriceInGwei == 0) {
            currentGasPriceInGwei = 1
        }
        presenter?.calculateTransactionFee(currentGasPriceInGwei)
    }

    override fun onBalanceFetched(balance: BigDecimal, currency: Currency) {
        if (selectedWallet != null && currency == Currency.valueOf(selectedWallet!!.currency)) {
            val rounded = balance.round(MathContext(8, RoundingMode.HALF_EVEN))
            availableTv.setText(getString(R.string.text_balance_currency, rounded.toString(), currency.name))
        }
    }

    override fun onTransactionFeeCalculated(feeInEth: BigDecimal, gasPriceInGwei: Int) {
        var feeUnit = "ETH"
        var gasPriceUnit = "GWEI"
        transactionFeeInEth = feeInEth
        Log.d(LOG_TAG, "Transaction fee calulated: $transactionFeeInEth")
        networkFeeTv.text = getString(R.string.network_fee_with_four_variables, transactionFeeInEth.toString(), feeUnit, gasPriceInGwei.toString(), gasPriceUnit)
    }

    private fun mayRequestContacts(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
            Snackbar.make(recepientTv, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok,
                            { requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_READ_CONTACTS) })
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_READ_CONTACTS)
        }
        return false
    }

    private fun showQRCodeScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt(getString(R.string.scan_address))
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.setRequestCode(scannerRequestCode)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == scannerRequestCode) {
            if (data == null || !data.hasExtra(Intents.Scan.RESULT)) {
                showMessage(getString(R.string.scan_cancelled))
                return
            }

            val qrCode = data.getStringExtra(Intents.Scan.RESULT)
            if (qrCode.isEmpty()) {
                showMessage(getString(R.string.scan_cancelled))
                return
            }

            Log.d(LOG_TAG, "Adress scanned: $qrCode")
            recepientTv.setText(qrCode)
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete()
            }
        }
    }

    fun navigateToConfirmWithTransaction(tx: PendingTransaction) {
        val intent = Intent(this, ConfirmTransferActivity::class.java)
        intent.putExtra(AppConstants.EXTRA_TRANSACTION, tx)
        intent.putExtra(AppConstants.EXTRA_GAS_PRICE, currentGasPriceInGwei)
        intent.putExtra(AppConstants.EXTRA_TRANSACTION_FEE, transactionFeeInEth)
        startActivity(intent)
    }

    fun nextButtonClicked() {
        showProgress(true)
        validateInputs()
    }

    fun validateInputs() {
        val usernameOrAddress = recepientTv.text.toString()
        val amount = amountTv.text.toString()
        val message = messageTv.text.toString()

        if (!TextUtils.isNumeric(amount)) {
            this.onInvalidTransactionValueError()
            return
        }
        if (selectedWallet == null) {
            this.onWalletError()
            return
        }

        presenter?.validateTransfer(usernameOrAddress = usernameOrAddress, value = BigDecimal(amount), transactionFee = transactionFeeInEth ?: BigDecimal.ZERO, wallet = selectedWallet!!, message = message)
    }

    override fun onInvalidRecipient() {
        showProgress(false)
        showMessage(getString(R.string.error_invalid_recipient))
    }

    override fun onInvalidTransactionValueError() {
        showProgress(false)
        showMessage(getString(R.string.invalid_transaction_value))
    }

    override fun onUserNotFound(username: String) {
        showProgress(false)
        showMessage(getString(R.string.error_user_not_in_contacts, username, username))
    }

    override fun onWalletError() {
        showProgress(false)
        showMessage(getString(R.string.error_load_wallet))
    }

    override fun onInsufficientEthBalanceError() {
        showProgress(false)
        showMessage(getString(R.string.error_insufficient_eth_balance))
    }

    override fun onInsufficientBalanceError() {
        showProgress(false)
        showMessage(getString(R.string.error_insufficient_balance_for_fee))
    }

    override fun onSendPendingTransaction(tx: PendingTransaction) {
        showProgress(false)
        navigateToConfirmWithTransaction(tx)
    }

    override fun onContactsFetched(list: List<User>) {
        adapter?.setSuggestionList(list)
    }

    override fun onContactsFetchError(error: Throwable) {
        showMessage(error.localizedMessage)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (wallets.size > position) {
            this.selectedWallet = wallets[position]
            presenter?.walletSelected(this.selectedWallet!!)
        } else {
            this.onWalletError()
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        layoutForm.visibility = if (show) View.GONE else View.VISIBLE
        layoutForm.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        layoutForm.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

        login_progress.visibility = if (show) View.VISIBLE else View.GONE
        login_progress.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        login_progress.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
    }

}
