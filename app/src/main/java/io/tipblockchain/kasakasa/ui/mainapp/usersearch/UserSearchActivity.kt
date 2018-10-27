package io.tipblockchain.kasakasa.ui.mainapp.usersearch

import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_user_search.*

class UserSearchActivity : BaseActivity(), UserSearch.View {

    lateinit var presenter: UserSearchPresenter
    lateinit var mAdapter: UserSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_search)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        presenter = UserSearchPresenter()
        presenter.attach(this)

        recyclerView.setHasFixedSize(true)
        mAdapter = UserSearchAdapter()
        recyclerView.adapter = mAdapter
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

    override fun showError(message: String) {
        super.showMessage(message)
    }

    override fun refreshSearchList(users: List<User>) {
        mAdapter.setResults(users)
    }
}
