package org.fossasia.susi.ai.skills

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.ChatActivity
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.Utils.hideSoftKeyboard
import org.fossasia.susi.ai.login.LoginActivity
import org.fossasia.susi.ai.login.LoginLogoutModulePresenter
import org.fossasia.susi.ai.login.contract.ILoginLogoutModulePresenter
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.signup.SignUpActivity
import org.fossasia.susi.ai.skills.groupwiseskills.GroupWiseSkillsFragment
import org.fossasia.susi.ai.skills.privacy.PrivacyFragment
import org.fossasia.susi.ai.skills.settings.ChatSettingsFragment
import org.fossasia.susi.ai.skills.settings.SettingsPresenter
import org.fossasia.susi.ai.skills.settings.contract.ISettingsPresenter
import org.fossasia.susi.ai.skills.skillSearch.SearchSkillFragment
import org.fossasia.susi.ai.skills.skilldetails.SkillDetailsFragment
import org.fossasia.susi.ai.skills.skilllisting.SkillListingFragment
import timber.log.Timber

/**
 * <h1>The Skills activity.</h1>
 * <h2>This activity is used to display SUSI Skills in the app.</h2>
 *
 * Created by mayanktripathi on 07/07/17.
 */

class SkillsActivity : AppCompatActivity(), SkillFragmentCallback {

    private val TAG_SETTINGS_FRAGMENT = "SettingsFragment"
    private val TAG_SKILLS_FRAGMENT = "SkillsFragment"
    private val TAG_PRIVACY_FRAGMENT = "PrivacyFragment"
    private val TAG_GROUP_WISE_SKILLS_FRAGMENT = "GroupWiseSkillsFragment"

    private var searchAction: MenuItem? = null
    private var isSearchOpened = false
    private var edtSearch: EditText? = null
    private var skills: ArrayList<Pair<String, List<SkillData>>> = ArrayList()
    private var text: String = ""
    private var group: String = ""
    private lateinit var settingsPresenter: ISettingsPresenter
    private lateinit var loginLogoutModulePresenter: ILoginLogoutModulePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
        setContentView(R.layout.activity_skills)

        settingsPresenter = SettingsPresenter(this)
        loginLogoutModulePresenter = LoginLogoutModulePresenter(this)
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
            // skills = skillFragment.skills
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

            R.id.menu_login -> {
                handleOnLoadingFragment()
                if (!settingsPresenter.getAnonymity()) {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(R.string.logout_confirmation).setCancelable(false).setPositiveButton(R.string.action_log_out) { _, _ ->
                        loginLogoutModulePresenter.logout()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                    val alert = builder.create()
                    alert.setTitle(getString(R.string.logout))
                    alert.show()
                } else {
                    loginLogoutModulePresenter.logout()
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

            R.id.action_search -> {
                handleMenuSearch()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun handleMenuSearch() {
        val action = supportActionBar // get the actionbar

        if (isSearchOpened) { // test if the search is open
            hideSoftKeyboard(this, window.decorView)
            action?.setDisplayShowCustomEnabled(false) // disable a custom view inside the actionbar
            action?.setDisplayShowTitleEnabled(true) // show the title in the action bar
            // add the search icon in the action bar
            searchAction?.icon = resources.getDrawable(R.drawable.ic_open_search)
            isSearchOpened = false
        } else { // open the search entry
            action?.setDisplayShowCustomEnabled(true) // enable it to display a
            // custom view in the action bar.
            action?.setCustomView(R.layout.search_bar) // add the custom view
            action?.setDisplayShowTitleEnabled(false) // hide the title

            edtSearch = action?.customView?.findViewById(R.id.edtSearch) // the text editor
            edtSearch?.setOnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP && edtSearch?.text.toString().isNotEmpty()) {
                        performSearch(edtSearch?.text.toString())
                }
                true
            }
            edtSearch?.requestFocus()

            // open the keyboard focused in the edtSearch
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT)
            // add the close icon
            searchAction?.icon = resources.getDrawable(R.drawable.ic_close_search)
            isSearchOpened = true
        }
    }

    /*
     Used to perform search for the query entered by the user
     Returns true if the skill is found related to search query else false
     */
    fun performSearch(query: String): Boolean {

        var searchedSkillsList: ArrayList<SkillData> = arrayListOf()

        if (skills.isEmpty()) {
            Toast.makeText(this, R.string.skill_empty, Toast.LENGTH_SHORT).show()
            return true
        }

        for (skill in skills) {
            var skillDataList: List<SkillData> = skill.second
            for (skillData in skillDataList) {
                if (skillData.skillName != "" && skillData.skillName != null) {
                    if (skillData.skillName.toLowerCase().contains(query.toLowerCase())) {
                        searchedSkillsList.add(skillData)
                    }
                }
            }
        }
        Timber.d(searchedSkillsList.toString())
        if (searchedSkillsList.isEmpty()) {
            Toast.makeText(this, R.string.skill_not_found, Toast.LENGTH_SHORT).show()
            return false
        }

        loadSearchSkillsFragment(searchedSkillsList, query)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        searchAction = menu?.findItem(R.id.action_search)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        when (currentFragment) {
            is SkillListingFragment -> menu?.setGroupVisible(R.id.menu_items, true)
            else -> menu?.setGroupVisible(R.id.menu_items, false)
        }
        val signUpMenuItem = menu?.findItem(R.id.menu_signup)
        val loginMenuItem = menu?.findItem(R.id.menu_login)

        if (!settingsPresenter.getAnonymity()) {
            loginMenuItem?.setTitle(getString(R.string.action_log_out))
            signUpMenuItem?.setVisible(false)
            signUpMenuItem?.setEnabled(false)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun loadSearchSkillsFragment(searchedSkills: ArrayList<SkillData>, searchQuery: String) {
        handleOnLoadingFragment()
        val skillSearchFragment = SearchSkillFragment.newInstance(searchedSkills, searchQuery)
        (this).supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, skillSearchFragment)
                .addToBackStack(SearchSkillFragment().toString())
                .commit()
    }

    override fun loadDetailFragment(skillData: SkillData?, skillGroup: String?, skillTag: String) {
        handleOnLoadingFragment()
        val skillDetailsFragment = SkillDetailsFragment.newInstance(skillData, skillGroup, skillTag)
        (this).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, skillDetailsFragment)
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
            val action = supportActionBar // get the actionbar
            action?.setDisplayShowCustomEnabled(false) // disable a custom view inside the actionbar
            action?.setDisplayShowTitleEnabled(true)
            searchAction?.icon = ContextCompat.getDrawable(this, R.drawable.ic_open_search)
            isSearchOpened = false
        }
    }
}
