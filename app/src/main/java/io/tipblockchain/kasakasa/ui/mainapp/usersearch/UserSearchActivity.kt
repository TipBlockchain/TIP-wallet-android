package io.tipblockchain.kasakasa.ui.mainapp.usersearch

import android.content.DialogInterface
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_user_search.*

class UserSearchActivity : BaseActivity(), UserSearch.View {

    lateinit var presenter: UserSearchPresenter
    lateinit var mAdapter: UserSearchAdapter
    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as User
            Log.d(LOG_TAG, "user is ${item}")
            showAddToContactsDialog(user = item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_search)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupPresenter()
        setupRecyclerView()
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_user_search, menu)
        val searchItem: MenuItem = menu!!.findItem(R.id.app_bar_search)
        val searchView: SearchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                presenter.instantSearch(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                presenter.instantSearch(query)
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSearchSetupError(err: Throwable) {
        showMessage(getString(R.string.error_setting_up_search, err.localizedMessage))
    }

    override fun onContactAdded(contact: User) {
        showMessage(getString(R.string.message_added_to_contacts, contact.username))
    }

    override fun onContactAddError(err: Throwable) {
        showOkDialog(message = getString(R.string.error_adding_contact, err.localizedMessage))
    }

    override fun refreshSearchList(users: List<User>) {
        mAdapter.setResults(users)
    }

    private fun setupPresenter() {
        presenter = UserSearchPresenter()
        presenter.attach(this)
    }

    private fun setupRecyclerView() {
        recyclerView.setHasFixedSize(true)
        mAdapter = UserSearchAdapter(mOnClickListener)
        recyclerView.adapter = mAdapter
        val attrs = intArrayOf(android.R.attr.listDivider)

        val a = this.obtainStyledAttributes(attrs)
        val divider = a.getDrawable(0)
        val inset = resources.getDimensionPixelSize(R.dimen.list_divider_small_margin)

        val insetDivider = InsetDrawable(divider, inset, 0, inset, 0)
        a.recycle()

        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(insetDivider)
        recyclerView.addItemDecoration(itemDecoration)
    }

    private fun showAddToContactsDialog(user: User) {
        this.showOkCancelDialog(title = getString(R.string.title_add_to_contacts),
                message = getString(R.string.confirm_add_to_contacts, user.username),
                onClickListener = DialogInterface.OnClickListener {_, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            addToContacts(user)
                        }
                    }
        })
    }

    private fun addToContacts(user: User) {
        Log.d(LOG_TAG, "Adding ${user} to contacts")
        presenter.addToContacts(user)
    }
}
