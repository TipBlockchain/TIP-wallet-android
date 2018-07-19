package io.tipblockchain.kasakasaprototype.ui.mainapp

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.tipblockchain.kasakasaprototype.R
import io.tipblockchain.kasakasaprototype.data.Transaction

import io.tipblockchain.kasakasaprototype.ui.mainapp.dummy.TransactionsContentManager
import kotlinx.android.synthetic.main.fragment_wallet.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [WalletFragment.OnListFragmentInteractionListener] interface.
 */
class WalletFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_wallet, container, false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        sendBtn.setOnClickListener { view ->
            Snackbar.make(view, "Send Payment", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        receiveBtn.setOnClickListener { view ->
            Snackbar.make(view, "Receive payment", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

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
}
