package io.tipblockchain.kasakasa.ui.views

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.View

class EmptyRecyclerView(context: Context, attrs: AttributeSet?, defStyle: Int): RecyclerView(context, attrs, defStyle) {
    private var mEmptyView: View? = null
    private val LOG_TAG = javaClass.name

    constructor(context: Context): this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)


    private fun initEmptyView() {
        Log.d(LOG_TAG, "initing empty view")
        if (mEmptyView != null) {
            mEmptyView!!.visibility = (if (adapter == null || adapter!!.itemCount == 0) View.VISIBLE else View.GONE)
        }
        this@EmptyRecyclerView.visibility = if (adapter == null || adapter!!.itemCount == 0) View.GONE else View.VISIBLE
    }

    fun updateState() {
        initEmptyView()
    }

    val observer: RecyclerView.AdapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            Log.d(LOG_TAG, "onChanged")
            initEmptyView()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            Log.d(LOG_TAG, "onItemRangeInserted")
            initEmptyView()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            Log.d(LOG_TAG, "onItemRangeRemoved")
            initEmptyView()
        }
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        val oldAdapter = getAdapter()
        super.setAdapter(adapter)
        oldAdapter?.unregisterAdapterDataObserver(observer)
        adapter?.registerAdapterDataObserver(observer)
        Log.d(LOG_TAG, "adapter set, adding observer")
    }

    fun setEmptyView(view: View) {
        this.mEmptyView = view
        Log.d(LOG_TAG, "Setting empty view")
        initEmptyView()
    }
}