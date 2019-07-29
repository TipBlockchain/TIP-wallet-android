package io.tipblockchain.kasakasa.ui.mainapp.phonecontacts

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.widget.SimpleCursorAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.ui.BaseActivity

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [PhoneContactsFragment.OnListFragmentInteractionListener] interface.
 */
class PhoneContactsFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null

    lateinit var contactsList: ListView
    // Define variables for the contact the user selects
    // The contact's _ID value
    var contactId: Long = 0
    // The contact's LOOKUP_KEY
    var contactKey: String? = null
    // A content URI for the selected contact
    var contactUri: Uri? = null
    // An adapter that binds the result Cursor to the ListView
    private var cursorAdapter: SimpleCursorAdapter? = null

    private val FROM_COLUMNS: Array<String> = arrayOf(
          ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    )
    /*
     * Defines an array that contains resource ids for the layout views
     * that get the Cursor column contents. The id is pre-defined in
     * the Android framework, so it is prefaced with "android.R.id"
     */
    private val TO_IDS: IntArray = intArrayOf(R.id.item_number, R.id.content)

    private val PROJECTION: Array<String> = arrayOf(
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone._ID
//            ContactsContract.CommonDataKinds.Phone.NUMBER
    )
    private val SELECTION: String = ""
    private var selectionArgs: Array<String> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_phonecontact_list, container, false)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (context is OnListFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
//        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.also {
            contactsList = it.findViewById(R.id.listView)
            // Gets a CursorAdapter
            cursorAdapter = SimpleCursorAdapter(
                    it,
                    R.layout.fragment_phonecontact,
                    null,
                    FROM_COLUMNS, TO_IDS,
                    0
            )
            // Sets the adapter for the ListView
            contactsList.adapter = cursorAdapter
        }

        contactsList.onItemClickListener = this
    }

    override fun onStart() {
        super.onStart()
        requestPermissions()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun loadContacts() {
        LoaderManager.getInstance(this).initLoader(0, null, this)
    }

    private fun requestPermissions() {
        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.READ_CONTACTS)
                .withListener (object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report?.areAllPermissionsGranted() == true) {
                            loadContacts()
                        } else if (report?.isAnyPermissionPermanentlyDenied ?: false) {
                            showPermissionsDeniedDialog()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                        token?.continuePermissionRequest()
                    }
                }).check()
    }

    private fun showPermissionsDeniedDialog() {
        (activity as BaseActivity).showOkCancelDialog(getString(R.string.title_allow_camera_access), getString(R.string.permission_read_contacts_denied), onClickListener = object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                when(which) {
                    DialogInterface.BUTTON_POSITIVE ->  requestPermissions()
                }
            }
        })
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
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                PhoneContactsFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Cursor> {
//        selectionArgs[0] = ""
        // Starts the query
        return activity?.let {
            return CursorLoader(
                    it,
                    ContactsContract.Contacts.CONTENT_URI,
                    PROJECTION,
                    SELECTION,
                    selectionArgs,
                    null
            )
        } ?: throw IllegalStateException()
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        cursorAdapter?.swapCursor(cursor)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        cursorAdapter?.swapCursor(null)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
