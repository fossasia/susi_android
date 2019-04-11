package org.fossasia.susi.ai.profile

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_profile.profile_navigation
import org.fossasia.susi.ai.R

class ProfileActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_profile -> {
                val fragment = ProfileFragment()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.profile_fragment_container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile_fragment -> {
                val fragment = EditProfileFragment()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.profile_fragment_container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profile_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        if (savedInstanceState == null) {
            val fragment = ProfileFragment()
            supportFragmentManager.beginTransaction()
                    .replace(R.id.profile_fragment_container, fragment, fragment.javaClass.getSimpleName())
                    .commit()
        }
    }
}
