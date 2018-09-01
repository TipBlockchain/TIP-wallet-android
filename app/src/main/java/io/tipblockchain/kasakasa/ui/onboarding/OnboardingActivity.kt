package io.tipblockchain.kasakasa.ui.onboarding

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import io.tipblockchain.kasakasa.R

import kotlinx.android.synthetic.main.activity_onboarding.*
import kotlinx.android.synthetic.main.fragment_onboarding.view.*
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.widget.Button
import io.tipblockchain.kasakasa.ui.mainapp.MainTabActivity
import io.tipblockchain.kasakasa.ui.newaccount.ChoosePasswordActivity
import io.tipblockchain.kasakasa.ui.newaccount.ChooseUsernameActivity
import io.tipblockchain.kasakasa.ui.newaccount.EnterPhoneNumberActivity
import kotlin.math.log


class OnboardingActivity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    internal var pageTitles = arrayOf(String())
    internal var imageIds = IntArray(0)
    internal var pageDescriptions = arrayOf(String())
    internal var viewPager: ViewPager? = null
    internal var createButton: Button? = null
    internal var sharedPrefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

//        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        pageTitles = arrayOf(getString(R.string.welcome_to_tip), getString(R.string.smart_addresses), getString(R.string.search_and_discovery), getString(R.string.p2p_instant_messaging))
        pageDescriptions = arrayOf(getString(R.string.discovery_on_blockchain), getString(R.string.send_money_to_friends), getString(R.string.information_is_indexed), getString(R.string.chat_with_friends))
        imageIds = intArrayOf(R.drawable.tip_logo, R.drawable.onboarding_address, R.drawable.onboarding_search, R.drawable.onboarding_messaging)
        viewPager = findViewById(R.id.container)
        createButton = findViewById(R.id.createAccountButton)
        print("adding viewpager")
        viewPager?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                // Check if this is the page you want.
                if (position == viewPager!!.adapter.count - 1) {
                    createButton?.visibility = View.VISIBLE
                } else {
                    createButton?.visibility = View.INVISIBLE
                }
                print("page selected $position")
            }
        })

        sharedPrefs = applicationContext.getSharedPreferences(getString(R.string.default_prefs_file), 0)
//        val setupComplete: Boolean = sharedPrefs!!.getBoolean(getString(R.string.prefs_setup_complete), false)
//        if (setupComplete) {
//            val intent = Intent(this, MainTabActivity::class.java)
//
//            startActivity(intent)
//        }

        createButton?.setOnClickListener {
            this.startAccountCreation()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_onboarding, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun startAccountCreation() {
        val intent = Intent(this, ChoosePasswordActivity::class.java)
        intent.putExtra("keyIdentifier", "value")
        startActivity(intent)
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 4
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val activity = activity
            val rootView = inflater.inflate(R.layout.fragment_onboarding, container, false)
            var index = arguments.getInt(ARG_SECTION_NUMBER) - 1
            if (activity is OnboardingActivity) {
                rootView.sectionTitleView.text = activity.pageTitles[index] //getString(R.string.section_format, arguments?.getInt(ARG_SECTION_NUMBER))
                rootView.sectionDescriptionView.text = activity.pageDescriptions[index]
                rootView.sectionImageView.setImageDrawable(ContextCompat.getDrawable(activity, activity.imageIds[index]))
            }
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
