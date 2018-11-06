package io.tipblockchain.kasakasa.ui.mainapp.contactlist

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.ui.mainapp.usersearch.UserSearchActivity
import kotlinx.android.synthetic.main.fragment_contact_list.*
import android.graphics.drawable.InsetDrawable

class ContactListFragment: Fragment(), ContactList.View {

    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null

    private lateinit var presenter: ContactListPresenter

    private lateinit var mAdapter: ContactListAdapter

    private val LOG_TAG = this.javaClass.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
        setupPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_contact_list, container, false)

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            navigagteToUserSearch()
        }

        return view
    }

    override fun onStop() {
        presenter.detach()
        super.onStop()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener = InteractionListener()

        this.setupRecyclerView()
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
        fun onListFragmentInteraction(item: User)
    }

    inner class InteractionListener: OnListFragmentInteractionListener {

        override fun  onListFragmentInteraction(item: User) {
            val dialogFragment = TransactionOptionsDialogFragment()
            dialogFragment.show(fragmentManager, "transaction")
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
        Log.d(LOG_TAG, "Received contacts: ${contacts}")
        hideSpinner()
        if (contacts != null) {
            mAdapter.setResults(contacts.toMutableList())
        }
    }

    override fun onNoContacts() {
        hideSpinner()
    }

    override fun onContactsLoading() {
        showSpinner()
    }

    override fun onContactAdded(contact: User) {
        mAdapter.addContact(contact)
    }

    override fun onContactRemoved(contact: User) {
        mAdapter.removeContact(contact)
    }

    override fun onContactAddError(error: Throwable) {
    }

    override fun onContactRemoveError(error: Throwable) {
    }

    override fun onContactsLoadError(error: Throwable) {
        hideSpinner()
    }

    private fun showSpinner() {

    }

    private fun hideSpinner() {

    }

    private fun navigagteToUserSearch() {
        val intent = Intent(activity, UserSearchActivity::class.java)
        startActivity(intent)
        Log.d("ContactList", "Starting search")
    }

    private fun setupPresenter(){
        presenter = ContactListPresenter()
        presenter.attach(this)
        presenter.fetchContactList()
    }

    private fun setupRecyclerView() {
        mAdapter = ContactListAdapter(activity!!.baseContext, mutableListOf(), listener)
        recyclerView.adapter = mAdapter

        val attrs = intArrayOf(android.R.attr.listDivider)

        val a = context!!.obtainStyledAttributes(attrs)
        val divider = a.getDrawable(0)
        val leftInset = resources.getDimensionPixelSize(R.dimen.list_divider_large_margin)
        val rightInset = resources.getDimensionPixelSize(R.dimen.list_divider_small_margin)

        val insetDivider = InsetDrawable(divider, leftInset, 0, rightInset, 0)
        a.recycle()

        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(insetDivider)
        recyclerView.addItemDecoration(itemDecoration)
    }
}