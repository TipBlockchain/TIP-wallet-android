package io.tipblockchain.kasakasa.ui.mainapp.tradetip

import android.content.Intent
import android.net.Uri
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.data.responses.CryptoExchange


/**
 * A placeholder fragment containing a simple view.
 */
class TradeTipActivityFragment : Fragment() {
    private var columnCount = 1
    private var listener: OnListFragmentInteractionListener = InteractionListener()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_trade_tip, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)
        if (rv is RecyclerView) {
            with(rv) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = TradeItemRecyclerViewAdapter(context, items, listener)
            }
        }
        return view
    }


    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: CryptoExchange?)
    }

    inner class InteractionListener: OnListFragmentInteractionListener {
        override fun onListFragmentInteraction(item: CryptoExchange?) {
            if (item != null) {
                val url = item.url
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
        }
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                TradeTipActivityFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }

    private val items: List<CryptoExchange> = listOf(
            CryptoExchange(name = "IDEX", displayName = "idex.market", url = "https://idex.market/eth/tip", logoId = R.drawable.exchange_idex),
            CryptoExchange(name = "Panxora", displayName = "panxora.io", url = "https://trading.panxora.io/register?refrence=TIP", logoId = R.drawable.exchange_panxora)
    )
}
