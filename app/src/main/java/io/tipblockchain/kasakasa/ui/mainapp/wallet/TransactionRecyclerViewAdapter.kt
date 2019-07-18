package io.tipblockchain.kasakasa.ui.mainapp.wallet

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.data.db.entity.Transaction
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.repository.UserRepository


import io.tipblockchain.kasakasa.ui.mainapp.wallet.WalletFragment.OnListFragmentInteractionListener

import kotlinx.android.synthetic.main.row_transaction.view.*
import org.web3j.utils.Convert
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class TransactionRecyclerViewAdapter(
        private var mContext: Context,
        private var mValues: List<Transaction>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<TransactionRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private val currentUser = UserRepository.currentUser

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

        val fromAddress = transaction.from
        var addressToShow: String

        var otherUser: User? = null
        if (currentUser?.address == fromAddress) {
            if (transaction.toUser != null) {
                otherUser = transaction.toUser
                addressToShow = transaction.toUser!!.username
            } else {
                addressToShow = transaction.to
            }
            holder.mAmountView.setTextColor(ContextCompat.getColor(mContext, R.color.sentRed))
        } else {
            if (transaction.fromUser != null) {
                otherUser = transaction.fromUser
                addressToShow = transaction.fromUser!!.username
            } else {
                addressToShow = transaction.from
            }
            holder.mAmountView.setTextColor(ContextCompat.getColor(mContext, R.color.receivedGreen))
        }

        val photoUrl = otherUser?.originalPhotoUrl
        if (photoUrl != null) {
            Picasso.get().load(photoUrl).into(holder.mAvartarIv)
        } else {
            Picasso.get().load(R.drawable.avatar_placeholder_small).into(holder.mAvartarIv)
        }
        holder.mUsernameView.text = addressToShow
        holder.mMessageTv.text = transaction.message
        val valueInEth =  Convert.fromWei(transaction.value.toBigDecimal(), Convert.Unit.ETHER)

        val currentScale = valueInEth.scale()
        holder.mAmountView.text = "${valueInEth.setScale(Math.min(currentScale, 4), RoundingMode.HALF_UP)} ${transaction.currency}"
        val timestamp = transaction.time.toLong() * 1000
        holder.mTimeTv.text = SimpleDateFormat("MM/dd/yy hh':'mm").format(Date(timestamp))

        with(holder.mView) {
            tag = transaction
            setOnClickListener(mOnClickListener)
        }
    }

    fun setItems(items: List<Transaction>) {
        mValues = items
        notifyDataSetChanged()
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
