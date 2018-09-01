package io.tipblockchain.kasakasa.ui.mainapp

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.data.Contact


import io.tipblockchain.kasakasa.ui.mainapp.ContactFragment.OnListFragmentInteractionListener

import kotlinx.android.synthetic.main.row_contact.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyContactRecyclerViewAdapter(
        private val context: Context,
        private val mValues: List<Contact>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MyContactRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Contact
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mIdView.text = item.alias
        holder.mContentView.text = item.name
        Picasso.get().load(item.avatarUrl).into(holder.mAvatarImageView)

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.fistnameTv
        val mContentView: TextView = mView.messageTv
        val mAvatarImageView: ImageView = mView.avatarImageView

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
