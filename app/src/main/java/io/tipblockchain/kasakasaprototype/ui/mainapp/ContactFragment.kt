package io.tipblockchain.kasakasaprototype.ui.mainapp

import android.app.Dialog
import android.app.ProgressDialog.show
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.tipblockchain.kasakasaprototype.R
import io.tipblockchain.kasakasaprototype.data.Contact

import io.tipblockchain.kasakasaprototype.ui.mainapp.dummy.ContactsContentManager
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import kotlinx.android.synthetic.main.fragment_contact_list.*
import android.content.DialogInterface
import android.content.Intent
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import io.tipblockchain.kasakasaprototype.ui.newaccount.ChoosePasswordActivity


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [ContactFragment.OnListFragmentInteractionListener] interface.
 */
class ContactFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null

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
                adapter = MyContactRecyclerViewAdapter(context, ContactsContentManager.ITEMS, listener)
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = InteractionListener()
//        if (context is OnListFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
//        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list.addItemDecoration(DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL))
    }

    override fun onDetach() {
        super.onDetach()
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
        fun onListFragmentInteraction(item: Contact)
    }

    inner class InteractionListener: OnListFragmentInteractionListener {
        override fun  onListFragmentInteraction(item: Contact) {
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
                ContactFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }

}

class TransactionOptionsDialogFragment: DialogFragment() {

    val contactFragment: ContactFragment? = null

    override  fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.prompt_send_or_request)
                .setItems(R.array.transaction_options, DialogInterface.OnClickListener { dialog, which ->
                    // The 'which' argument contains the index position
                    // of the selected item
                    if (which == 0) {
                        showRequestPaymentScreen()
                    } else {
                        showSendPaymentScreen()
                    }
                })
        return builder.create()
    }


    fun showRequestPaymentScreen() {
        val intent = Intent(activity, SendTransferActivity::class.java)
        intent.putExtra("keyIdentifier", "value")
        startActivity(intent)
    }

    fun showSendPaymentScreen() {
        val intent = Intent(activity, SendTransferActivity::class.java)
        intent.putExtra("keyIdentifier", "value")
        startActivity(intent)
    }
}
