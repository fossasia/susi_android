package org.fossasia.susi.ai.skills

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.chat.ChatActivity
import org.fossasia.susi.ai.helper.Utils
import org.fossasia.susi.ai.helper.Utils.hideSoftKeyboard
import org.fossasia.susi.ai.rest.responses.susi.SkillData
import org.fossasia.susi.ai.skills.aboutus.AboutUsFragment
import org.fossasia.susi.ai.skills.groupwiseskills.GroupWiseSkillsFragment
import org.fossasia.susi.ai.skills.settings.ChatSettingsFragment
import org.fossasia.susi.ai.skills.skilldetails.SkillDetailsFragment
import org.fossasia.susi.ai.skills.skilllisting.SearchFragment
import org.fossasia.susi.ai.skills.skilllisting.SkillListingFragment
import org.fossasia.susi.ai.skills.skilllisting.SkillListingPresenter

/**
 * <h1>The Skills appActivity.</h1>
 * <h2>This appActivity is used to display SUSI Skills in the app.</h2>
 *
 * Created by mayanktripathi on 07/07/17.
 */

class SkillsActivity : AppCompatActivity(), SkillFragmentCallback {

    private val TAG_SETTINGS_FRAGMENT = "SettingsFragment"
    private val TAG_SKILLS_FRAGMENT = "SkillsFragment"
    private val TAG_ABOUT_FRAGMENT = "AboutUsFragment"
    private val TAG_GROUP_WISE_SKILLS_FRAGMENT = "GroupWiseSkillsFragment"
    var searchAction: MenuItem? = null
    var isSearchOpened = false
    val SEARCH_FRAGMENT_TAG = "search_fragment_tag"
    private lateinit var skillNames: ArrayList<SkillData>

    private var skills: ArrayList<Pair<String, List<SkillData>>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
        setContentView(R.layout.activity_skills)

        val skillFragment = SkillListingFragment()
        //skills = skillFragment.skills
        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, skillFragment, TAG_SKILLS_FRAGMENT)
                .addToBackStack(TAG_SKILLS_FRAGMENT)
                .commit()

        supportFragmentManager.addOnBackStackChangedListener {
            invalidateOptionsMenu()
        }
        skills = skillFragment.skills
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        when (fragment) {
            is SearchFragment -> {
                val action = supportActionBar
                action?.setDisplayShowCustomEnabled(false)
                action?.setDisplayShowTitleEnabled(true)
                isSearchOpened = false
                searchAction?.icon = resources.getDrawable(R.drawable.ic_open_search)
                openSkillsFragment(supportFragmentManager)
            }
            is SkillListingFragment -> {
                finish()

                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
                val intent = Intent(this, ChatActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            else -> return super.onBackPressed()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)

        SkillListingPresenter.showMenuIcon.apply {
            value = false
        }
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        when (currentFragment) {

            is SkillListingFragment -> {
                menu?.setGroupVisible(R.id.menu_items, true)

                SkillListingPresenter.showMenuIcon.observe(this, Observer {
                    it?.let {
                        if (it)
                            searchAction = menu?.findItem(R.id.action_search)
                        menu?.findItem(R.id.action_search)?.isVisible = it
                    }
                    if (it == null) {
                        menu?.findItem(R.id.action_search)?.isVisible = false
                    }
                })
            }
            else -> menu?.setGroupVisible(R.id.menu_items, false)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.skills_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        skillNames = skillNames()

        when (item.itemId) {
            android.R.id.home -> {
                onHomeButtonPressed()
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
            R.id.action_search -> {
                supportActionBar?.setCustomView(R.layout.search_bar)//add the custom view
                handleMenuSearch()
                openSearchFragment(supportFragmentManager, skillNames, SEARCH_FRAGMENT_TAG)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun gotoSkillFragment(fragmentManager: FragmentManager) {

        if (supportFragmentManager.findFragmentById(R.id.fragment_container) is SkillListingFragment) {
            finish()

            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
            val intent = Intent(this, ChatActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            return
        } else openSkillsFragment(fragmentManager)
    }

    private fun onHomeButtonPressed() {
        // If search fragment wasn't found in the backstack...

        gotoSkillFragment(supportFragmentManager)
    }

    private fun openSearchFragment(
        fragmentManager: FragmentManager,

        skillNames: ArrayList<SkillData>,
        searcH_FRAGMENT_TAG: String
    ) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.custom_fade_in, R.animator.custom_fade_out, R.animator.custom_fade_in, R.animator.custom_fade_out)
                .replace(R.id.fragment_container, SearchFragment.getInstance(skillNames), searcH_FRAGMENT_TAG)
                .commit()
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

    private fun handleOnLoadingFragment() {
        hideSoftKeyboard(this, window.decorView)
        if (isSearchOpened) {
            val action = supportActionBar //get the actionbar
            action?.setDisplayShowCustomEnabled(false) //disable a custom view inside the actionbar
            action?.setDisplayShowTitleEnabled(true)

            isSearchOpened = false
        }
    }

    private fun skillNames(): ArrayList<SkillData> {
        val skillsData: ArrayList<SkillData> = ArrayList()
        val skillList = skills
                .flatMap { it.second }.asSequence()
                .distinctBy { skillData -> skillData.skillName }
                .filter { skillData ->
                    !skillData.skillName.isNullOrBlank() &&
                            skillData.skillName != getString(R.string.no_skill_name)
                }.toList()

        skillsData.addAll(skillList)
        return skillsData
    }

    private fun handleMenuSearch() {
        val action = supportActionBar //get the actionbar
        if (isSearchOpened) { //test if the search is open
            Utils.hideSoftKeyboard(this, window.decorView)
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

            val edtSearch = action?.customView?.findViewById<EditText>(R.id.edtSearch) //the text editor

            edtSearch?.requestFocus()
            //open the keyboard focused in the edtSearch
            if (edtSearch != null) {
                Utils.showSoftKeyboard(this, edtSearch)
            }

            isSearchOpened = true
        }
    }

    private fun openSkillsFragment(fragmentManager: FragmentManager) {
        val action = supportActionBar
        action?.setDisplayShowCustomEnabled(false)
        action?.setDisplayShowTitleEnabled(true)
        isSearchOpened = false
        searchAction?.icon = resources.getDrawable(R.drawable.ic_open_search)
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SkillListingFragment())
                .addToBackStack(TAG_SKILLS_FRAGMENT)
                .commit()
    }
}
