package io.tipblockchain.kasakasa.ui.mainapp.tradetip

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.data.responses.CryptoExchange
import kotlinx.android.synthetic.main.fragment_trade_tip_list_item.view.*

class TradeItemRecyclerViewAdapter(
        private val mContext: Context,
        private val mValues: List<CryptoExchange>,
        private val mListener: TradeTipActivityFragment.OnListFragmentInteractionListener
    ): RecyclerView.Adapter<TradeItemRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as CryptoExchange
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_trade_tip_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return mValues.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val exchange = mValues[position]
        viewHolder.mExchangeTv.text = exchange.displayName
        viewHolder.mExchangeIv.setImageResource(exchange.logoId)
        with(viewHolder.mView) {
            tag = exchange
            setOnClickListener(mOnClickListener)
        }
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {

        val mExchangeTv: TextView = mView.exchangeNameTv
        val mExchangeIv: ImageView = mView.exchangeIv

        override fun toString(): String {
            return super.toString() + " '" + mExchangeTv.text + "'"
        }
    }
}