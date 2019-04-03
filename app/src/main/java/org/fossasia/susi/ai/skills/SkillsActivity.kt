package org.fossasia.susi.ai.skills

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_skill_listing.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.ChatActivity
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.Utils.hideSoftKeyboard
import org.fossasia.susi.ai.login.LoginActivity
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.signup.SignUpActivity
import org.fossasia.susi.ai.skills.aboutus.AboutUsFragment
import org.fossasia.susi.ai.skills.groupwiseskills.GroupWiseSkillsFragment
import org.fossasia.susi.ai.skills.help.HelpFragment
import org.fossasia.susi.ai.skills.privacy.PrivacyFragment
import org.fossasia.susi.ai.skills.settings.ChatSettingsFragment
import org.fossasia.susi.ai.skills.settings.SettingsPresenter
import org.fossasia.susi.ai.skills.settings.contract.ISettingsPresenter
import org.fossasia.susi.ai.skills.skilldetails.SkillDetailsFragment
import org.fossasia.susi.ai.skills.skilllisting.SkillListingFragment

/**
 * <h1>The Skills activity.</h1>
 * <h2>This activity is used to display SUSI Skills in the app.</h2>
 *
 * Created by mayanktripathi on 07/07/17.
 */

class SkillsActivity : AppCompatActivity(), SkillFragmentCallback {

    private val TAG_SETTINGS_FRAGMENT = "SettingsFragment"
    private val TAG_SKILLS_FRAGMENT = "SkillsFragment"
    private val TAG_ABOUT_FRAGMENT = "AboutUsFragment"
    private val TAG_HELP_FRAGMENT = "HelpFragment"
    private val TAG_PRIVACY_FRAGMENT = "PrivacyFragment"
    private val TAG_GROUP_WISE_SKILLS_FRAGMENT = "GroupWiseSkillsFragment"

    private var searchAction: MenuItem? = null
    private var isSearchOpened = false
    private var edtSearch: EditText? = null
    private var skills: ArrayList<Pair<String, List<SkillData>>> = ArrayList()
    private var text: String = ""
    private var group: String = ""
    private lateinit var settingsPresenter: ISettingsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
        setContentView(R.layout.activity_skills)

        settingsPresenter = SettingsPresenter(this)
        val skillFragment = SkillListingFragment()
        val privacyFragment = PrivacyFragment()
        val bundle = intent.extras
        val isSignupToPrivacy = bundle?.getBoolean(Constant.SIGN_UP_TO_PRIVACY)
        if (isSignupToPrivacy == true) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, privacyFragment, TAG_PRIVACY_FRAGMENT)
                    .addToBackStack(TAG_PRIVACY_FRAGMENT)
                    .commit()
        } else {
            //skills = skillFragment.skills
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, skillFragment, TAG_SKILLS_FRAGMENT)
                    .addToBackStack(TAG_SKILLS_FRAGMENT)
                    .commit()
        }
        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            currentFragment?.onResume()
        }

        supportFragmentManager.addOnBackStackChangedListener {
            invalidateOptionsMenu()
        }

        skills = skillFragment.skills
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.skills_activity_menu, menu)
        if (!settingsPresenter.getAnonymity()) {
            val loginMenuItem = menu?.findItem(R.id.menu_login)
            loginMenuItem?.setTitle("Logout")
            val signUpMenuItem = menu?.findItem(R.id.menu_signup)
            signUpMenuItem?.setVisible(false)
            signUpMenuItem?.setEnabled(false)
        }
        return true
    }

    private fun backHandler(context: Context) {
        val lastFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (lastFragment == null) {
            finish()
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
            val intent = Intent(context, ChatActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        if (!isSearchOpened) {
            super.onBackPressed()
            backHandler(this)
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
        } else {
            val action = supportActionBar
            action?.setDisplayShowCustomEnabled(false)
            action?.setDisplayShowTitleEnabled(true)
            searchAction?.icon = resources.getDrawable(R.drawable.ic_open_search)
            isSearchOpened = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.menu_settings -> {
                handleOnLoadingFragment()
                val settingsFragment = ChatSettingsFragment()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, settingsFragment, TAG_SETTINGS_FRAGMENT)
                        .addToBackStack(TAG_SETTINGS_FRAGMENT)
                        .commit()
            }

            R.id.menu_about -> {
                handleOnLoadingFragment()
                val aboutFragment = AboutUsFragment()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, aboutFragment, TAG_ABOUT_FRAGMENT)
                        .addToBackStack(TAG_ABOUT_FRAGMENT)
                        .commit()
            }
            R.id.menu_help -> {
                handleOnLoadingFragment()
                val helpFragment = HelpFragment()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, helpFragment, TAG_HELP_FRAGMENT)
                        .addToBackStack(TAG_HELP_FRAGMENT)
                        .commit()
            }

            R.id.menu_login -> {
                handleOnLoadingFragment()
                if (!settingsPresenter.getAnonymity()) {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(R.string.logout_confirmation).setCancelable(false).setPositiveButton(R.string.action_log_out) { _, _ ->
                        settingsPresenter.loginLogout()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                    val alert = builder.create()
                    alert.setTitle(getString(R.string.logout))
                    alert.show()
                } else {
                    settingsPresenter.loginLogout()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }

            R.id.menu_signup -> {
                handleOnLoadingFragment()
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }

            R.id.menu_privacy -> {
                handleOnLoadingFragment()
                val aboutFragment = PrivacyFragment()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, aboutFragment, TAG_PRIVACY_FRAGMENT)
                        .addToBackStack(TAG_PRIVACY_FRAGMENT)
                        .commit()
            }

            R.id.action_search -> {
                handleMenuSearch()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun handleMenuSearch() {
        val action = supportActionBar //get the actionbar

        if (isSearchOpened) { //test if the search is open
            hideSoftKeyboard(this, window.decorView)
            action?.setDisplayShowCustomEnabled(false) //disable a custom view inside the actionbar
            action?.setDisplayShowTitleEnabled(true) //show the title in the action bar
            //add the search icon in the action bar
            searchAction?.icon = resources.getDrawable(R.drawable.ic_open_search)
            isSearchOpened = false
        } else { //open the search entry

            action?.setDisplayShowCustomEnabled(true) //enable it to display a
            // custom view in the action bar.
            action?.setCustomView(R.layout.search_bar)//add the custom view
            action?.setDisplayShowTitleEnabled(false) //hide the title

            edtSearch = action?.customView?.findViewById(R.id.edtSearch) //the text editor

            //this is a listener to do a search when users enters query in editText
            edtSearch?.addTextChangedListener(object : TextWatcher {
                /**
                 * Variable used to remove unneccessary invoking of doSearch() method.
                 */
                var skillFound = true

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    //no need to implement
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //no need to implement
                }

                override fun afterTextChanged(s: Editable?) {
                    val currentText = s.toString()
                    //checking that value exist in skills and if not exist comparing the length of the current text to the previous text where the skills are present
                    if (skillFound || (currentText.length <= text.length)) {
                        skillFound = performSearch(currentText)
                        text = currentText
                    } else {
                        Toast.makeText(baseContext, R.string.skill_not_found, Toast.LENGTH_SHORT).show()
                    }
                }
            })
            edtSearch?.requestFocus()

            //open the keyboard focused in the edtSearch
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT)
            //add the close icon
            searchAction?.icon = resources.getDrawable(R.drawable.ic_close_search)
            isSearchOpened = true
        }
    }

    /*
     Used to perform search for the query entered by the user
     Returns true if the skill is found related to search query else false
     */
    fun performSearch(query: String): Boolean {

        for ((pos, item) in skills.withIndex()) {
            if (query in item.first) {
                skillMetrics.scrollToPosition(pos)
                return true
            }

            for (item2 in item.second) {
                if (query.toLowerCase() in item2.group.toLowerCase()) {
                    skillMetrics.scrollToPosition(pos)
                    return true
                }
            }
        }
        Toast.makeText(this, R.string.skill_not_found, Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        searchAction = menu?.findItem(R.id.action_search)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        when (currentFragment) {
            is SkillListingFragment -> menu?.setGroupVisible(R.id.menu_items, true)
            else -> menu?.setGroupVisible(R.id.menu_items, false)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun loadDetailFragment(skillData: SkillData?, skillGroup: String?, skillTag: String) {
        handleOnLoadingFragment()
        val skillDetailsFragment = SkillDetailsFragment.newInstance(skillData, skillGroup, skillTag)
        (this).supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, skillDetailsFragment)
                .addToBackStack(SkillDetailsFragment().toString())
                .commit()
    }

    override fun loadGroupWiseSkillsFragment(group: String) {
        handleOnLoadingFragment()
        val groupWiseSkillsFragment = GroupWiseSkillsFragment.newInstance(group)
        (this).supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, groupWiseSkillsFragment)
                .addToBackStack(GroupWiseSkillsFragment().toString())
                .commit()
    }

    fun handleOnLoadingFragment() {
        hideSoftKeyboard(this, window.decorView)
        if (isSearchOpened) {
            val action = supportActionBar //get the actionbar
            action?.setDisplayShowCustomEnabled(false) //disable a custom view inside the actionbar
            action?.setDisplayShowTitleEnabled(true)
            searchAction?.icon = ContextCompat.getDrawable(this, R.drawable.ic_open_search)
            isSearchOpened = false
        }
    }
}
