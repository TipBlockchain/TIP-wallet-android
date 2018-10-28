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

class UserSearchAdapter: RecyclerView.Adapter<UserSearchAdapter.ViewHolder> {

    private var mValues: List<User> = listOf()
    private var mOnClickListener: View.OnClickListener

    constructor(onClickListener: View.OnClickListener): super() {
        mOnClickListener = onClickListener
    }

    fun setResults(users: List<User>) {
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
            Picasso.get().load(R.drawable.avatar_placeholder_small).into(holder.mAvatarImageView)
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