package io.tipblockchain.kasakasa.ui.mainapp

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main_tab.*

import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.mainapp.contactlist.ContactListFragment
import io.tipblockchain.kasakasa.ui.mainapp.transactions.WalletFragment
import io.tipblockchain.kasakasa.ui.mainapp.myaccount.MyAccountFragment

class MainTabActivity : BaseActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {

            R.id.navigation_contacts -> {
                mSectionsPagerAdapter?.replaceFragment(ContactListFragment.newInstance(1))
//                supportActionBar?.show()
                supportActionBar?.setTitle(R.string.title_contacts)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_wallet -> {
//                supportActionBar?.hide()
                supportActionBar?.setTitle(R.string.title_wallet)
                mSectionsPagerAdapter?.replaceFragment(WalletFragment.newInstance(1))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_account -> {
                supportActionBar?.show()
                supportActionBar?.setTitle(R.string.title_my_account)
                mSectionsPagerAdapter?.replaceFragment(MyAccountFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_tab)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        viewPager.adapter = mSectionsPagerAdapter

        supportActionBar?.setTitle(R.string.title_contacts)

        this.addStartingFragment()
    }

    override fun onResume() {
        super.onResume()
        Log.d(LOG_TAG, "OnResume: Empty Contact")
    }

    private fun addStartingFragment() {
        mSectionsPagerAdapter?.replaceFragment(ContactListFragment.newInstance(1))
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment = when (position) {
            0 -> PlaceholderFragment.newInstance(position + 1)
            1 -> WalletFragment()
            2 -> ContactListFragment()
            else -> PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }

        fun replaceFragment(newFragment: Fragment) {
            val transaction = supportFragmentManager.beginTransaction()

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
            transaction.replace(R.id.container, newFragment)
            transaction.addToBackStack(null)

// Commit the transaction
            transaction.commit()
        }

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_transaction_list, container, false)
            return rootView
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }
}
