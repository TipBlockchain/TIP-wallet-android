package io.tipblockchain.kasakasa.ui.mainapp.contactlist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.data.db.entity.User
import kotlinx.android.synthetic.main.row_contact.view.*

class ContactListAdapter (
    private var context: Context,
    private var mValues: MutableList<User>,
    private var mListener: ContactListFragment.OnListFragmentInteractionListener?) : RecyclerView.Adapter<ContactListAdapter.ViewHolder>() {

        private val mOnClickListener: View.OnClickListener

        init {
            mOnClickListener = View.OnClickListener { v ->
                val item = v.tag as User
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
            holder.mIdView.text = item.username
            holder.mContentView.text = item.name
            if (item.originalPhotoUrl != null) {
                Picasso.get().load(item.originalPhotoUrl).into(holder.mAvatarImageView)
            } else {
                Picasso.get().load(R.drawable.avatar_placeholder_small).into(holder.mAvatarImageView)
            }

            with(holder.mView) {
                tag = item
                setOnClickListener(mOnClickListener)
            }
        }

        fun setResults(contacts: MutableList<User>) {
            mValues = contacts
            notifyDataSetChanged()
        }

        fun addContact(contact: User) {
            mValues.add(contact)
            notifyItemInserted(mValues.size -1)
        }

        fun removeContact(contact: User) {
            var index = mValues.indexOf(contact)
            if (index >= 0) {
                mValues.remove(contact)
                notifyItemRemoved(index)
            }
        }

        override fun getItemCount(): Int = mValues.size

        inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
            val mIdView: TextView = mView.fistnameTv
            val mContentView: TextView = mView.usernameTv
            val mAvatarImageView: ImageView = mView.avatarImageView

            override fun toString(): String {
                return super.toString() + " '" + mContentView.text + "'"
            }
        }
}