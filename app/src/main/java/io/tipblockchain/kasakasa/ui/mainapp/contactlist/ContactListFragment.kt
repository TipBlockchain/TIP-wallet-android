package io.tipblockchain.kasakasa.ui.mainapp.contactlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.ui.mainapp.TransactionOptionsDialogFragment
import io.tipblockchain.kasakasa.ui.mainapp.usersearch.UserSearchActivity
import kotlinx.android.synthetic.main.fragment_contact_list.*

class ContactListFragment: Fragment(), ContactList.View {

    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null

    private lateinit var presenter: ContactListPresenter

    private lateinit var mAdapter: ContactListRecyclerViewAdapter
    private var mContacts: MutableSet<User> = mutableSetOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_contact_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = ContactListRecyclerViewAdapter(context, mContacts.toTypedArray().toList(), listener)
                mAdapter = adapter!! as ContactListRecyclerViewAdapter
            }
        }
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            navigagteToUserSearch()
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        presenter = ContactListPresenter()
        presenter.attach(this)
        listener = InteractionListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list.addItemDecoration(DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL))

    }

    override fun onDetach() {
        super.onDetach()
        presenter.detach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: User)
    }

    inner class InteractionListener: OnListFragmentInteractionListener {
        override fun  onListFragmentInteraction(item: User) {
            Log.e("error", "List fragent interraction for ${item}")
            val newFragment = TransactionOptionsDialogFragment()
            newFragment.show(fragmentManager, "missiles")
        }
    }


    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                ContactListFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }

    override fun onContactsFetched(contacts: List<User>?) {
        if (contacts  != null) {
            mContacts.addAll(contacts)
        }
        mAdapter.notifyDataSetChanged()
    }

    override fun onNoContacts() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onContactsLoadError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onContactsLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onContactAdded(contact: User) {
        mContacts.add(contact)
        mAdapter.notifyDataSetChanged()
    }

    override fun onContactRemoved() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun navigagteToUserSearch() {
        val intent = Intent(activity, UserSearchActivity::class.java)
        startActivity(intent)
        Log.d("ContactList", "Starting search")
    }
}