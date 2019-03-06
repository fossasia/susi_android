package org.fossasia.susi.ai.skills.skilllisting

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_search.items_list
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.helper.Utils
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.skills.LayoutManagerSmoothScroller
import org.fossasia.susi.ai.skills.SkillFragmentCallback
import org.fossasia.susi.ai.skills.skilllisting.AnimationUtils.animateView
import org.fossasia.susi.ai.skills.skilllisting.adapters.recycleradapters.SkillListAdapter
import timber.log.Timber

/**
 * fragment to show the list of results for the search
 */
open class SearchFragment : Fragment() {
    lateinit var edtSearch: EditText
    private var textWatcher: TextWatcher? = null
    private var searchString: String? = null
    lateinit var appActivity: AppCompatActivity
    private lateinit var skillNames: ArrayList<SkillData>
    private lateinit var skillCallback: SkillFragmentCallback
    private lateinit var infoListAdapter: SkillListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    /**
     * companion object to provide an instance of Search Fragment
     */
    companion object {
        /**
         * @param skillNames [SkillData] list to be provided to the Search Fragment to perform search upon.
         */
        fun getInstance(skillNames: ArrayList<SkillData>): SearchFragment {
            val searchFragment = SearchFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList("KEY_ARRAYLIST", skillNames)
            searchFragment.arguments = bundle
            return searchFragment
        }
    }


    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)

        when (context) {
            is AppCompatActivity -> this.appActivity = context as AppCompatActivity
        }
        skillNames = arguments?.getParcelableArrayList("KEY_ARRAYLIST")!!
        skillNames.sortBy {
            it.skillName

        }
        if (context is SkillFragmentCallback) {
            skillCallback = context as SkillFragmentCallback
        } else {
            Timber.e("context is not SkillFragmentCallback")
        }
        infoListAdapter = SkillListAdapter(appActivity, skillNames, skillCallback, search = true)


    }

    private fun hideKeyboardSearch() {
        if (edtSearch == null) return


        Utils.hideSoftKeyboard(appActivity, edtSearch)

        if (edtSearch != null) edtSearch.clearFocus()
    }

    override fun onPause() {
        super.onPause()
        hideKeyboardSearch()

    }

    override fun onResume() {
        super.onResume()

        if (TextUtils.isEmpty(searchString)) {
            showKeyboardSearch()
            showSuggestionsPanel()
        } else {
            hideKeyboardSearch()
            hideListPanel()
        }
    }

    private fun hideListPanel() {
        if (items_list != null) animateView(items_list, false, 200)
    }

    private fun showKeyboardSearch() {
        if (edtSearch == null) return

        if (edtSearch.requestFocus()) {
            Utils.showSoftKeyboard(appActivity, edtSearch)
        }


    }


    override fun onViewCreated(rootView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)
        val editText = appActivity?.supportActionBar?.customView?.findViewById<EditText>(R.id.edtSearch)
        if (editText != null) edtSearch = editText
        edtSearch.setText(searchString)
        initViews()

        initSearchListeners()
        infoListAdapter.submitList(skillNames)
    }


    private fun initViews() {

        items_list.adapter = infoListAdapter
        items_list.layoutManager = LayoutManagerSmoothScroller(appActivity)
    }

    private fun showSuggestionsPanel() {
        if (items_list != null) animateView(items_list, true, 200)
    }


    private fun initSearchListeners() {

        edtSearch.setOnClickListener {

            showSuggestionsPanel()

        }

        edtSearch.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
            if (hasFocus) {
                showSuggestionsPanel()
            }
        }



        if (textWatcher != null) edtSearch.removeTextChangedListener(textWatcher)
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                val newText = edtSearch.text.toString()
                // search newText in the list
                performSearch(newText)

            }
        }
        edtSearch.addTextChangedListener(textWatcher)
        edtSearch.setOnEditorActionListener { _, _, event ->
            if (event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER || event.action == EditorInfo.IME_ACTION_SEARCH)) {
                performSearch(edtSearch.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun performSearch(query: String) {
        val result = ArrayList<SkillData>()
        var flag = 0

        skillNames
                .filter { item ->
                    Timber.d("item $item")

                    item.skillName.toLowerCase().contains(query.toLowerCase())
                }
                .also { results ->
                    Timber.d("$results")
                    result += results
                    Timber.d("$result")

                    if (result.isNotEmpty()) flag = 1
                }

        Timber.d("flag $flag")


        when (flag) {
            1 -> {
                items_list.smoothScrollToPosition(0)
                infoListAdapter.setSkillList(result)
                infoListAdapter.submitList(result)


            }
            else -> {
                infoListAdapter.submitList(result)
                Toast.makeText(context, "No skill found", Toast.LENGTH_SHORT).show()
            }
        }
    }


}

