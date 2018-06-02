package org.fossasia.susi.ai.skills.skillactivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_skill_listing.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.ChatActivity
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.skills.aboutus.AboutUsFragment
import org.fossasia.susi.ai.skills.settings.ChatSettingsFragment
import org.fossasia.susi.ai.skills.skillactivity.contract.ISkillActivityPresenter
import org.fossasia.susi.ai.skills.skillactivity.contract.ISkillActivityView
import org.fossasia.susi.ai.skills.skilldetails.SkillDetailsFragment
import org.fossasia.susi.ai.skills.skilllisting.SkillListingFragment


/**
 * <h1>The Skills activity.</h1>
 * <h2>This activity is used to display SUSI Skills in the app.</h2>
 *
 * Created by mayanktripathi on 07/07/17.
 */

class SkillsActivity : AppCompatActivity(), SkillFragmentCallback, ISkillActivityView {

    private val TAG_SETTINGS_FRAGMENT = "SettingsFragment"
    private val TAG_SKILLS_FRAGMENT = "SkillsFragment"
    private val TAG_ABOUT_FRAGMENT = "AboutUsFragment"

    private var mSearchAction: MenuItem? = null
    private var edtSearch: EditText? = null
    private var skills: ArrayList<Pair<String, Map<String, SkillData>>> = ArrayList()
    lateinit var skillActivityPresenter: ISkillActivityPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
        setContentView(R.layout.activity_skills)

        skillActivityPresenter = SkillActivityPresenter(this)
        skillActivityPresenter.onAttach(this)

        val skillFragment = SkillListingFragment()
        skills = skillFragment.skills
        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, skillFragment, TAG_SKILLS_FRAGMENT)
                .addToBackStack(TAG_SKILLS_FRAGMENT)
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.skills_activity_menu, menu)
        return true
    }

    private fun exitActivity() {
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
        val intent = Intent(this@SkillsActivity, ChatActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onBackPressed() {
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
        if (supportFragmentManager.popBackStackImmediate(TAG_SKILLS_FRAGMENT, 0)) {
            title = getString(R.string.skills_activity)
        } else {
            finish()
            exitActivity()
        }
    }

    override fun  hideKeyboard() {
        val inputManager:InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.SHOW_FORCED)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.menu_settings -> {
                val settingsFragment = ChatSettingsFragment()
               if(skillActivityPresenter?.isSearchOpened())
                   skillActivityPresenter?.handleMenuSearch()
                supportFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, settingsFragment, TAG_SETTINGS_FRAGMENT)
                        .addToBackStack(TAG_SETTINGS_FRAGMENT)
                        .commit()
            }

            R.id.menu_about -> {
                val aboutFragment = AboutUsFragment()
                supportFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, aboutFragment, TAG_ABOUT_FRAGMENT)
                        .addToBackStack(TAG_ABOUT_FRAGMENT)
                        .commit()
            }

            R.id.action_search -> {
                handleMenuSearch()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun handleMenuSearch() {
        skillActivityPresenter?.handleMenuSearch()
    }

    override fun closeSearch() {
        val action = supportActionBar //get the actionbar
        action!!.setDisplayShowCustomEnabled(false) //disable a custom view inside the actionbar
        action.setDisplayShowTitleEnabled(true) //show the title in the action bar
        //add the search icon in the action bar
        mSearchAction?.icon = resources.getDrawable(R.drawable.ic_open_search)
    }

    override fun openSearch() {
        val action = supportActionBar //get the actionbar
        action!!.setDisplayShowCustomEnabled(true) //enable it to display a
        // custom view in the action bar.
        action.setCustomView(R.layout.search_bar)//add the custom view
        action.setDisplayShowTitleEnabled(false) //hide the title
        edtSearch = action.customView.findViewById(R.id.edtSearch) as EditText //the text editor
        //this is a listener to do a search when the user clicks on search button
        edtSearch?.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch((findViewById(R.id.edtSearch) as EditText).text.toString())
                return@OnEditorActionListener true
            }
            false
        })
        edtSearch?.requestFocus()
        //open the keyboard focused in the edtSearch
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT)
        //add the close icon
        mSearchAction?.icon = resources.getDrawable(R.drawable.ic_close_search)
    }

    fun doSearch(query: String) {

        for ((pos, item) in skills.withIndex()) {
            if (query in item.first) {
                skillGroups.scrollToPosition(pos)
                return
            }

            for (item2 in item.second.keys) {
                if (query.toLowerCase() in item2) {
                    skillGroups.scrollToPosition(pos)
                    return
                }
            }
        }
        Toast.makeText(this, R.string.skill_not_found, Toast.LENGTH_SHORT).show()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        mSearchAction = menu?.findItem(R.id.action_search)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun loadSkillDetailFragment(skillTag: String, skillData: SkillData, skillGroup: String) {
        if(skillActivityPresenter?.isSearchOpened())
            skillActivityPresenter?.handleMenuSearch()

        val skillDetailsFragment = SkillDetailsFragment.newInstance(skillData, skillGroup, skillTag)
        (this).supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, skillDetailsFragment)
                .addToBackStack(SkillDetailsFragment().toString())
                .commit()
    }

    override fun startChatActivity(position: Int, examples: List<String>) {
        (this as Activity).overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
        val intent = Intent(this, ChatActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        if (examples != null && examples.isNotEmpty() && examples[position] != null)
            intent.putExtra("example", examples[position])
        else
            intent.putExtra("example", "")
        this.startActivity(intent)
        (this as Activity).finish()
    }
}
