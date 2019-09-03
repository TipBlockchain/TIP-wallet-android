package io.tipblockchain.kasakasa.ui.mainapp.walletlist

import android.content.Intent
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.app.AppConstants
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.ui.mainapp.wallet.WalletActivity
import kotlinx.android.synthetic.main.fragment_wallet_list.recyclerView

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [WalletListFragment.OnListFragmentInteractionListener] interface.
 */
class WalletListFragment : Fragment(), WalletList.View {


    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = ListInteractionListener()
    private var presenter: WalletListPresenter? = null
    private var recyclerViewAdapter: WalletListRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
        presenter = WalletListPresenter()
        presenter?.attach(this)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wallet_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setupRecyclerView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter?.loadWallets()
    }

    override fun onDetach() {
        super.onDetach()
        presenter?.detach()
        listener = null
        presenter = null
    }

    override fun showWallets(wallets: List<Wallet>) {
        recyclerViewAdapter?.setValues(wallets)
    }

    private fun setupRecyclerView() {
        if (recyclerView is RecyclerView) {

            val adapter = WalletListRecyclerViewAdapter(listOf(), listener)
            with(recyclerView) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                this.adapter = adapter
            }
            this.recyclerViewAdapter = adapter
        }

        val attrs = intArrayOf(android.R.attr.listDivider)

        val a = context!!.obtainStyledAttributes(attrs)
        val divider = a.getDrawable(0)
        val leftInset = resources.getDimensionPixelSize(R.dimen.list_divider_large_margin)
        val rightInset = resources.getDimensionPixelSize(R.dimen.list_divider_small_margin)

        val insetDivider = InsetDrawable(divider, leftInset, 0, rightInset, 0)
        a.recycle()

        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(insetDivider)
        recyclerView.addItemDecoration(itemDecoration)
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
        fun onListFragmentInteraction(item: Wallet?)
    }

    inner class ListInteractionListener: OnListFragmentInteractionListener {
        override fun onListFragmentInteraction(item: Wallet?) {
            if (item != null) {
                showWallet(item)
            }
        }
    }

    fun showWallet(wallet: Wallet) {
        val intent = Intent(this.context, WalletActivity::class.java)
        intent.putExtra(AppConstants.EXTRA_CURRENT_WALLET, wallet)
        context?.startActivity(intent)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                WalletListFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
