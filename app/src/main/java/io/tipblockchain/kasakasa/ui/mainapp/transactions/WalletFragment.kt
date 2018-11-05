package io.tipblockchain.kasakasa.ui.mainapp.transactions

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.data.db.entity.Transaction
import io.tipblockchain.kasakasa.data.db.repository.Currency

import kotlinx.android.synthetic.main.fragment_wallet.*
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [WalletFragment.OnListFragmentInteractionListener] interface.
 */
class WalletFragment : Fragment(), AdapterView.OnItemSelectedListener, WalletInterface.View {

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null
    private var presenter: WalletPresenter? = null

    private val logTag = javaClass.name
    private var lastCurrency: Currency = Currency.TIP
    private var adapter: TransactionRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
        setupPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_wallet, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recyclerView.addItemDecoration(DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL))

        if (recyclerView is RecyclerView) {
            with(recyclerView) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
//                adapter = MyTransactionRecyclerViewAdapter(TransactionsContentManager.ITEMS, listener)
            }
        } else {
            Log.w("TAG", "Transaction list is NOT a recyclerView, it is a ${recyclerView}")
        }
        adapter = TransactionRecyclerViewAdapter(listOf(), listener)
        recyclerView.adapter = adapter

        sendBtn.setOnClickListener { v ->
            Snackbar.make(v, "Send Payment", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        receiveBtn.setOnClickListener { v ->
            Snackbar.make(v, "Receive payment", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onResume() {
        super.onResume()
        currencySelected(lastCurrency)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        Log.d(logTag, "Creating options menu")
        inflater?.inflate(R.menu.menu_currency_options, menu)
        val item: MenuItem = menu?.findItem(R.id.spinner) as MenuItem
        val spinner: Spinner = item.actionView as Spinner
        val currencyOptions = listOf("TIP", "ETH")
        var menuAdapter: ArrayAdapter<String> = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, currencyOptions)
        spinner.adapter = menuAdapter
        spinner.dropDownVerticalOffset = getActionBarHeight()
        spinner.dropDownHorizontalOffset = 0
        spinner.onItemSelectedListener = this
    }

    private fun getActionBarHeight(): Int {
        val styledAttributes = context!!.theme.obtainStyledAttributes(
                intArrayOf(android.R.attr.actionBarSize))
        val barSize = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()
        return barSize
    }

    private fun currencySelected(currency: Currency) {
        lastCurrency = currency
        presenter?.switchCurrency(lastCurrency)
    }

    override fun onTransactionsFetchError(error: Throwable?, currency: Currency) {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (context is OnListFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        presenter?.detach()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: Transaction)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                WalletFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.d(logTag, "Nothing selected")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Log.d(logTag, "Menu item selected: position: $position, id: $id")
        when (position) {
            0 -> currencySelected(Currency.TIP)
            1 -> currencySelected(Currency.ETH)
        }
    }


    override fun onBalanceFetched(address: String, currency: Currency, balance: BigDecimal) {
        balanceTv.text = "${balance.setScale(4, RoundingMode.HALF_UP).toString()} ${currency.name}"
    }

    override fun onTransactionsFetched(address: String, currency: Currency, transactions: List<Transaction>) {
        if (currency != lastCurrency) {
            return
        }
        adapter?.setItems(transactions)
    }

    private fun setupPresenter() {
        presenter = WalletPresenter()
        presenter?.attach(this)
    }

}
