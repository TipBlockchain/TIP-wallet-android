package io.tipblockchain.kasakasa.ui.mainapp.sendtransfer

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.data.db.entity.User


class UserFilterAdapter: ArrayAdapter<User>, Filterable {
    private var mContext: Context? = null
    private var mSuggestionList: List<User> = listOf()
    private var mFilteredSuggestionList: List<User> = listOf()

    constructor(context: Context, suggestionList: List<User>): super(context, -1) {
        mContext = context
        mSuggestionList = suggestionList
        mFilteredSuggestionList = mSuggestionList
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val viewHolder: ViewHolder
        if (view == null) {
            val inflater = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.row_user_suggestion, null)
            viewHolder = ViewHolder()
            viewHolder.fullnameTv = view.findViewById(R.id.fullnameTv) as TextView
            viewHolder.usernameTv = view.findViewById(R.id.usernameTv) as TextView
            viewHolder.imageView = view.findViewById(R.id.imageView) as ImageView
            view.setTag(viewHolder)
        } else {
            viewHolder = view.tag as ViewHolder
        }
        val searchSuggestion = mFilteredSuggestionList[position]
        viewHolder.usernameTv?.text = searchSuggestion.username
        viewHolder.fullnameTv?.text = searchSuggestion.name
//        viewHolder.imageView?.setImageResource(if (searchSuggestion.isTag()) R.drama`wable.suggestion_tag_icon else R.drawable.suggestion_project_icon)
        return view!!
    }

    fun setSuggestionList(list: List<User>) {
        mSuggestionList = list
        mFilteredSuggestionList = listOf()
        notifyDataSetChanged()
    }

    class ViewHolder {
        var imageView: ImageView? = null
        var usernameTv: TextView? = null
        var fullnameTv: TextView? = null
    }

    override fun getCount(): Int {
        return mFilteredSuggestionList.size
    }

    override fun getItem(position: Int): User {
        val user = mFilteredSuggestionList[position]
        Log.d("FILTER", "Gettign item: position: $position: $user")
        return user
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val constraintString = constraint.toString().toLowerCase()
                if (!constraintString.isEmpty()) {
                    val tempFilteredList = mutableListOf<User>()
                    for (suggestion in mSuggestionList) {
                        val name = suggestion.name
                        if (name.toLowerCase().contains(constraintString) ||
                                suggestion.username.toLowerCase().contains(constraintString)) {
                            tempFilteredList.add(suggestion)
                        }
                    }
                    results.values = tempFilteredList
                    results.count = tempFilteredList.size
                } else {
                    results.values = mSuggestionList
                    results.count = mSuggestionList.size
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                Log.d("FILTER", "Publishing results for $constraint: $results")
                if (results.values != null) {
                    mFilteredSuggestionList = results.values as List<User>
                    notifyDataSetChanged()
                }
            }
        }
    }
}