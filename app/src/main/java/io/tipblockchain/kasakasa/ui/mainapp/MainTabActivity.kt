package io.tipblockchain.kasakasa.ui.mainapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main_tab.*

import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.app.AppConstants
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.mainapp.contactlist.ContactListFragment
import io.tipblockchain.kasakasa.ui.mainapp.more.MoreFragment
import io.tipblockchain.kasakasa.ui.mainapp.wallet.WalletFragment
import io.tipblockchain.kasakasa.ui.mainapp.walletlist.WalletListFragment
import android.net.Uri

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

    private var activeFragment: Fragment? = null
    private val contactListFragment = ContactListFragment.newInstance(1)
    private val walletFragment = WalletListFragment.newInstance(1)
    private val moreFragment = MoreFragment.newInstance(0)

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        Log.d(LOG_TAG, "Selecting item with id: ${item.itemId}")
        when (item.itemId) {
            R.id.navigation_contacts -> {
                mSectionsPagerAdapter?.replaceFragment(contactListFragment)
                activeFragment = contactListFragment
                supportActionBar?.setTitle(R.string.title_contacts)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_wallet -> {
                supportActionBar?.setTitle(R.string.title_wallet)
                mSectionsPagerAdapter?.replaceFragment(walletFragment)
                activeFragment = walletFragment
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_account -> {
                supportActionBar?.setTitle(R.string.title_more)
                mSectionsPagerAdapter?.replaceFragment(moreFragment)
                activeFragment = moreFragment
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
        viewPager.offscreenPageLimit = 3

        supportActionBar?.setTitle(R.string.title_contacts)

        this.addStartingFragment()
    }

    override fun onResume() {
        super.onResume()
        Log.d(LOG_TAG, "OnResume: Empty Contact")
    }

    override fun onBackPressed() {
        if (activeFragment == contactListFragment) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    fun addFragment(fragment: Fragment) {
        activeFragment = fragment
        mSectionsPagerAdapter?.replaceFragment(fragment,addToBackStack = true)
    }

    private fun addStartingFragment() {
        mSectionsPagerAdapter?.replaceFragment(contactListFragment, addToBackStack = false)
        activeFragment = contactListFragment
    }

    fun selectContact() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        startActivityForResult(intent, AppConstants.REQUEST_CODE_PICK_CONTACTS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.REQUEST_CODE_PICK_CONTACTS -> {
                if (resultCode == Activity.RESULT_OK) {
                    val projection: Array<String> = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)

                    // Get the URI that points to the selected contact
                    data?.data?.also { contactUri ->
                        // Perform the query on the contact to get the NUMBER column
                        // We don't need a selection or sort order (there's only one result for this URI)
                        // CAUTION: The query() method should be called from a separate thread to avoid
                        // blocking your app's UI thread. (For simplicity of the sample, this code doesn't
                        // do that.)
                        // Consider using <code><a href="/reference/android/content/CursorLoader.html">CursorLoader</a></code> to perform the query.
                        contentResolver.query(contactUri, projection, null, null, null)?.apply {
                            moveToFirst()

                            // Retrieve the phone number from the NUMBER column
                            val column: Int = getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            val number: String? = getString(column)
                            if (number != null) {
                                sendInviteMessage(number)
                            } else {
                                // show error dialog.
                                showOkDialog(title = getString(R.string.error_sending_message), message = getString(R.string.error_sending_message_body))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun sendInviteMessage(phoneNumber: String) {
        val uri = Uri.parse("smsto:$phoneNumber")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("sms_body", getString(R.string.message_invite))
        startActivity(intent)
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

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//            super.destroyItem(container, position, `object`)
        }

        fun replaceFragment(newFragment: Fragment, addToBackStack: Boolean = true) {
            val transaction = supportFragmentManager.beginTransaction()

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
            transaction.replace(R.id.container, newFragment)
            if (addToBackStack) {
                transaction.addToBackStack(null)
            }

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

    interface Resumable {
        fun resume()
    }
}
