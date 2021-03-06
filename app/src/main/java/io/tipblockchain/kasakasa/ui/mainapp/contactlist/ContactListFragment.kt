package io.tipblockchain.kasakasa.ui.mainapp.contactlist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import android.support.v4.content.LocalBroadcastManager
import android.widget.Button
import io.tipblockchain.kasakasa.app.AppConstants
import io.tipblockchain.kasakasa.ui.mainapp.MainTabActivity
import java.util.*
import kotlin.concurrent.timerTask
import io.tipblockchain.kasakasa.ui.mainapp.phonecontacts.PhoneContactsFragment


class ContactListFragment: Fragment(), ContactList.View {

    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null

    private var presenter: ContactListPresenter? = null

    private lateinit var mAdapter: ContactListAdapter

    private val LOG_TAG = this.javaClass.name

    private val contactReceiver = ContactReceiver()
    private lateinit var tabActivity: MainTabActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(AppConstants.ACTION_CONTACT_ADDED)
        LocalBroadcastManager.getInstance(activity!!).registerReceiver(contactReceiver, intentFilter)
        Log.d(LOG_TAG, "Onstart called. Receiver registered")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_contact_list, container, false)

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            navigateToUserSearch()
        }

        var inviteBtn = view.findViewById<Button>(R.id.inviteBtn)
        inviteBtn.setOnClickListener {
            this.selectPhoneContacts()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.tabActivity = activity as MainTabActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener = InteractionListener()
        userVisibleHint = false
        this.setupRecyclerView()
        this.setupPresenter()
    }

    override fun onStop() {
        presenter?.detach()
        super.onStop()
        listener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(contactReceiver)
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
            var bundle = Bundle()
            bundle.putString("user", item.username)
            dialogFragment.arguments = bundle
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

    private fun showPhoneContacts() {
        val newFragment = PhoneContactsFragment.newInstance(1)
        this.tabActivity.addFragment(newFragment)
    }

    private fun selectPhoneContacts() {
        this.tabActivity.selectContact()
    }

    inner class ContactReceiver: BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(LOG_TAG, "Contact added intent")
            if (intent != null && intent.action == AppConstants.ACTION_CONTACT_ADDED) {
                val contact = intent.getSerializableExtra(AppConstants.EXTRA_CONTACT_ADDED) as? User
                if (contact != null) {
                    Log.d(LOG_TAG, "Contact added : ${contact}")
                    onContactAdded(contact)
                }
            }
        }
    }

    private fun navigateToUserSearch() {
        val intent = Intent(activity, UserSearchActivity::class.java)
        startActivity(intent)
    }

    private fun setupPresenter(){
        presenter = ContactListPresenter()
        presenter?.attach(this)
        presenter?.loadContactList()
        Timer().schedule(timerTask{
            presenter?.fetchContactList()
        }, 1000)
    }

    private fun setupRecyclerView() {
        mAdapter = ContactListAdapter(activity!!.baseContext, mutableListOf(), listener)
        recyclerView.adapter = mAdapter
        recyclerView.setEmptyView(view!!.findViewById(R.id.emptyView))
        val attrs = intArrayOf(android.R.attr.listDivider)

        val a = context!!.obtainStyledAttributes(attrs)
        val divider = a.getDrawable(0)
        val leftInset = resources.getDimensionPixelSize(R.dimen.list_divider_very_large_margin)
        val rightInset = resources.getDimensionPixelSize(R.dimen.list_divider_small_margin)

        val insetDivider = InsetDrawable(divider, leftInset, 0, rightInset, 0)
        a.recycle()

        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(insetDivider)
        recyclerView.addItemDecoration(itemDecoration)
    }
}