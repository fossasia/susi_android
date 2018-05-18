package org.fossasia.susi.ai.skills

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.fossasia.susi.ai.R
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import org.fossasia.susi.ai.chat.ChatActivity
import org.fossasia.susi.ai.skills.aboutus.AboutUsFragment
import org.fossasia.susi.ai.skills.settings.ChatSettingsFragment
import org.fossasia.susi.ai.skills.skilldetails.SkillDetailsFragment
import org.fossasia.susi.ai.skills.skilllisting.SkillListingFragment

import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.content.Context.INPUT_METHOD_SERVICE
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_skill_listing.*
import org.fossasia.susi.ai.rest.responses.susi.SkillData


/**
 * <h1>The Skills activity.</h1>
 * <h2>This activity is used to display SUSI Skills in the app.</h2>
 *
 * Created by mayanktripathi on 07/07/17.
 */

class SkillsActivity : AppCompatActivity() {

    val TAG_SETTINGS_FRAGMENT = "SettingsFragment"
    val TAG_SKILLS_FRAGMENT = "SkillsFragment"
    val TAG_ABOUT_FRAGMENT = "AboutUsFragment"

    private var mSearchAction: MenuItem? = null
    private var isSearchOpened = false
    private var edtSearch: EditText? = null
    private var skills : ArrayList<Pair<String, Map<String, SkillData>>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
        setContentView(R.layout.activity_skills)

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

    fun exitActivity() {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.menu_settings -> {
                val settingsFragment = ChatSettingsFragment()
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
                handleMenuSearch();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected fun handleMenuSearch() {
        val action = supportActionBar //get the actionbar

        if (isSearchOpened) { //test if the search is open

            action!!.setDisplayShowCustomEnabled(false) //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true) //show the title in the action bar

            //hides the keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(edtSearch?.getWindowToken(), 0)

            //add the search icon in the action bar
            mSearchAction?.setIcon(resources.getDrawable(R.drawable.ic_open_search))

            isSearchOpened = false
        } else { //open the search entry

            action!!.setDisplayShowCustomEnabled(true) //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar)//add the custom view
            action.setDisplayShowTitleEnabled(false) //hide the title

            edtSearch = action.customView.findViewById(R.id.edtSearch) as EditText //the text editor

            //this is a listener to do a search when the user clicks on search button
            edtSearch?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        doSearch((findViewById(R.id.edtSearch) as EditText).text.toString())
                        return true
                    }
                    return false
                }
            })


            edtSearch?.requestFocus()

            //open the keyboard focused in the edtSearch
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT)


            //add the close icon
            mSearchAction?.setIcon(resources.getDrawable(R.drawable.ic_close_search))

            isSearchOpened = true
        }
    }

    fun doSearch(query : String) {

        var pos = 0


        for( item in skills) {
            if(query in item.first){
                skillGroups.scrollToPosition(pos)
                return
            }

            for (item2 in item.second.keys){
                if( query.toLowerCase() in item2){
                    skillGroups.scrollToPosition(pos)
                    return
                }

            }

            pos++
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        mSearchAction = menu?.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu)
    }
}