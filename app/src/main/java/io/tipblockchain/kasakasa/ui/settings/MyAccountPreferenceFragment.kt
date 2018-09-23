package io.tipblockchain.kasakasa.ui.settings

import android.net.Uri
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat

import io.tipblockchain.kasakasa.R

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * [MyAccountPreferenceFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MyAccountPreferenceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyAccountPreferenceFragment : PreferenceFragmentCompat() {
    // The URL to +1.  Must be a valid URL.
    private val PLUS_ONE_URL = "http://developer.android.com"
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.pref_general)
    }

//    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//        // Inflate the layout for this fragment
//        val view = inflater!!.inflate(R.layout.fragment_my_account, container, false)
//
//        //Find the +1 button
//        mPlusOneButton = view.findViewById<View>(R.id.plus_one_button) as PlusOneButton
//
//        return view
//    }

    override fun onResume() {
        super.onResume()

        // Refresh the state of the +1 button each time the activity receives focus.
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }


    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"
        // The request code must be 0 or greater.
        private val PLUS_ONE_REQUEST_CODE = 0

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyAccountPreferenceFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): MyAccountPreferenceFragment {
            val fragment = MyAccountPreferenceFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
