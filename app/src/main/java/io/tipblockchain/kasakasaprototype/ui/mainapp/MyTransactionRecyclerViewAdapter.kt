package io.tipblockchain.kasakasaprototype.ui.mainapp

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.tipblockchain.kasakasaprototype.R
import io.tipblockchain.kasakasaprototype.data.Contact
import io.tipblockchain.kasakasaprototype.data.Transaction


import io.tipblockchain.kasakasaprototype.ui.mainapp.WalletFragment.OnListFragmentInteractionListener

import kotlinx.android.synthetic.main.row_transaction.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyTransactionRecyclerViewAdapter(
        private val mValues: List<Transaction>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MyTransactionRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Transaction
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = mValues[position]

        val otherUser: Contact = transaction.from
        holder.mUsernameView.text = otherUser.alias
        holder.mMessageTv.text = transaction.message
        holder.mAmountView.text = "${transaction.amount} TIP"
        holder.mTimeTv.text = transaction.time.toString()
        Picasso.get().load(otherUser.avatarUrl).into(holder.mAvartarIv)

        with(holder.mView) {
            tag = transaction
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mUsernameView: TextView = mView.fistnameTv
        var mAmountView: TextView = mView.amountTv
        val mMessageTv: TextView = mView.messageTv
        val mTimeTv: TextView = mView.timeTv
        val mAvartarIv: ImageView = mView.avatarIv

//        override fun toString(): String {
//            return super.toString() + " '" + mContentView.text + "'"
//        }
    }
}
