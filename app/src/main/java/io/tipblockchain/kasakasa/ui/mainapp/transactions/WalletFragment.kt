package io.tipblockchain.kasakasa.ui.mainapp.transactions

import android.content.Context
import android.database.DataSetObserver
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
import android.widget.SpinnerAdapter
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.data.db.entity.Transaction
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.ui.mainapp.MyTransactionRecyclerViewAdapter

import io.tipblockchain.kasakasa.ui.mainapp.dummy.TransactionsContentManager
import kotlinx.android.synthetic.main.fragment_wallet.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [WalletFragment.OnListFragmentInteractionListener] interface.
 */
class WalletFragment : Fragment(), AdapterView.OnItemSelectedListener, Wallet.View {

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null
    private var presenter: WalletPresenter? = null

    private val logTag = javaClass.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_wallet, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = WalletPresenter()
        presenter?.attach(this)

        transactionList.addItemDecoration(DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL))

        if (transactionList is RecyclerView) {
            with(transactionList) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyTransactionRecyclerViewAdapter(TransactionsContentManager.ITEMS, listener)
            }
        } else {
            Log.w("TAG", "Transaction list is NOT a recyclerView, it is a ${transactionList}")
        }

        sendBtn.setOnClickListener { v ->
            Snackbar.make(v, "Send Payment", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        receiveBtn.setOnClickListener { v ->
            Snackbar.make(v, "Receive payment", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        Log.d(logTag, "Creating options menu")
        inflater?.inflate(R.menu.menu_currency_options, menu)
        val item: MenuItem = menu?.findItem(R.id.spinner) as MenuItem
        val spinner: Spinner = item.actionView as Spinner
        val currencyOptions = listOf("TIP", "ETH")
        var adapter: ArrayAdapter<String> = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, currencyOptions)
        spinner.adapter = adapter
        spinner.layoutMode = Spinner.MODE_DROPDOWN

        spinner.onItemSelectedListener = this
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
    }


    override fun onBalanceFetched(address: String, currency: Currency) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTransactionsFetched(address: String, currency: Currency, transactions: List<Transaction>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
