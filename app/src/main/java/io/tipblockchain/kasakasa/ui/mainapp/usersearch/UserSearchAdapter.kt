package io.tipblockchain.kasakasa.ui.mainapp.usersearch

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

class UserSearchAdapter: RecyclerView.Adapter<UserSearchAdapter.ViewHolder>() {
    private var mValues: List<User> = listOf()
    private val placeholderImageUrl: String = "https://d1nhio0ox7pgb.cloudfront.net/_img/o_collection_png/green_dark_grey/256x256/plain/user.png"

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as User
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
        }
    }

    fun setResults(users: List<User>): Unit {
        mValues = users
        notifyDataSetChanged()
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
            Picasso.get().load(item.originalPhotoUrl!!).into(holder.mAvatarImageView)
        } else {
            Picasso.get().load(placeholderImageUrl).into(holder.mAvatarImageView)
        }

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
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