package io.tipblockchain.kasakasa.ui.mainapp.more

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.tipblockchain.kasakasa.R

import io.tipblockchain.kasakasa.ui.mainapp.more.MoreFragment.OnListFragmentInteractionListener
import io.tipblockchain.kasakasa.ui.mainapp.more.dummy.DummyContent.DummyItem

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyMoreItemRecyclerViewAdapter(
        private val mContext: Context,
        private val mValues: List<MoreListItem>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MyMoreItemRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as MoreListItem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item: MoreListItem = mValues[position]
//        while (item.isHeader) {
//            true -> return ViewTypes.Header.value
//        }
        Log.d("Adapter", "getItemViewType for position ${position}")
        if (item.isHeader) {
            return ViewTypes.Header.value
        } else {
            return ViewTypes.Content.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View
        Log.d("Adapter", "Creating view for type ${viewType}")
        if (viewType == ViewTypes.Header.value) {
            view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_more_list_header, parent, false)
            return HeaderViewHolder(view)
        } else {
            view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_more_list_item, parent, false)
            return ContentViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        Log.d("Adapter", "Binding View for position ${position}")
        Log.d("Adapter", "item is ${item}")
        if (holder is HeaderViewHolder) {
            holder.mTitleView.text = item.title
        } else if (holder is ContentViewHolder) {
            if (item.resourceId != null) {
                holder.mIconView.setImageDrawable(mContext.getDrawable(item.resourceId))
            }
            holder.mTitleView.text = item.title
        }

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    open class ViewHolder(val mView: View): RecyclerView.ViewHolder(mView){}

    inner class ContentViewHolder(mView: View) : ViewHolder(mView) {
        val mIconView: ImageView = mView.findViewById(R.id.iconImageView)
        val mTitleView: TextView = mView.findViewById(R.id.titleTv)

        override fun toString(): String {
            return super.toString() + " '" + mTitleView.text + "'"
        }
    }

    inner class HeaderViewHolder(mView: View) : ViewHolder(mView) {
        val mTitleView: TextView = mView.findViewById(R.id.titleTv)

        override fun toString(): String {
            return super.toString() + " '" + mTitleView.text + "'"
        }
    }

    enum class ViewTypes(val value: Int) {
        Header(1),
        Content(2)
    }
}
