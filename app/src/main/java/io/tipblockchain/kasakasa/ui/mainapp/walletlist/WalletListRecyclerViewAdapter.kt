package io.tipblockchain.kasakasa.ui.mainapp.walletlist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.data.db.repository.Currency

import io.tipblockchain.kasakasa.ui.mainapp.walletlist.WalletListFragment.OnListFragmentInteractionListener
import io.tipblockchain.kasakasa.ui.mainapp.walletlist.dummy.DummyContent.DummyItem

import kotlinx.android.synthetic.main.fragment_wallet_list_item.view.*
import org.web3j.utils.Convert
import java.math.BigInteger
import java.math.RoundingMode

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class WalletListRecyclerViewAdapter(
        private var mValues: List<Wallet>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<WalletListRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Wallet
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_wallet_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.currencyTextView.text = item.displayName()
        val logoResourceId = getResourceId(item)
        if (logoResourceId > 0) {
            holder.logoImageView.setImageResource(logoResourceId)
        }
        holder.setBalanceText(item.balance, item.currency)

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    fun setValues(values: List<Wallet>) {
        mValues = values
        notifyDataSetChanged()
    }

    private fun getResourceId(wallet: Wallet): Int {
        when (wallet.currency) {
            Currency.ETH.name -> return R.drawable.coin_logo_eth
            Currency.TIP.name -> return R.drawable.coin_logo_tip
        }
        return 0
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val logoImageView: ImageView = mView.logoIv
        val currencyTextView: TextView = mView.currencyTv
        val balanceTextView: TextView = mView.balanceTv

        override fun toString(): String {
            return super.toString() + " '" + currencyTextView.text + "'"
        }

        fun setBalanceText(balance: BigInteger, currency: String) {
            val valueInEth =  Convert.fromWei(balance.toBigDecimal(), Convert.Unit.ETHER)

            val currentScale = valueInEth.scale()
            balanceTextView.text = "${valueInEth.setScale(Math.min(currentScale, 4), RoundingMode.HALF_UP)} ${currency}"
        }
    }
}
