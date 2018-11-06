package io.tipblockchain.kasakasa.ui.mainapp.sendtransfer

import android.content.Context
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
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.row_user_suggestion, null)
            viewHolder = ViewHolder()
            viewHolder.fullnameTv = convertView.findViewById(R.id.fullnameTv) as TextView
            viewHolder.usernameTv = convertView.findViewById(R.id.usernameTv) as TextView
            viewHolder.imageView = convertView.findViewById(R.id.imageView) as ImageView
            convertView.setTag(viewHolder)
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        val searchSuggestion = mFilteredSuggestionList[position]
        viewHolder.usernameTv?.text = searchSuggestion.username
        viewHolder.fullnameTv?.text = searchSuggestion.name
//        viewHolder.imageView?.setImageResource(if (searchSuggestion.isTag()) R.drama`wable.suggestion_tag_icon else R.drawable.suggestion_project_icon)
        return convertView!!
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
        return mFilteredSuggestionList[position]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val constraintString = constraint.toString().toLowerCase()
                if (constraintString.isEmpty()) {
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
                if (results.values != null) {
                    mFilteredSuggestionList = results.values as List<User>
                    notifyDataSetChanged()
                }
            }
        }
    }
}