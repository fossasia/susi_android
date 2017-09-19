package org.fossasia.susi.ai.skills

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
        setContentView(R.layout.activity_skills)

        val skillFragment = SkillListingFragment()
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, skillFragment, TAG_SKILLS_FRAGMENT)
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
        val fragment = fragmentManager.findFragmentById(R.id.fragment_container)

        when(fragment){
            is SkillDetailsFragment ->  {
                fragmentManager.popBackStack()
                title = getString(R.string.skills_activity)
            }
            else -> {
               finish()
               exitActivity()
            }
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
                        .commit()
            }

            R.id.menu_about -> {
                val aboutFragment = AboutUsFragment()
                supportFragmentManager.beginTransaction()
                        .add(R.id.fragment_container,aboutFragment,TAG_ABOUT_FRAGMENT)
                        .commit()
            }
        }
        return super.onOptionsItemSelected(item);
    }
}