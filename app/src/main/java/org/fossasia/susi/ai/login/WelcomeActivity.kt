package org.fossasia.susi.ai.login

/* Created by Abhishek Poonia */

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_welcome.*
import org.fossasia.susi.ai.R

class WelcomeActivity : AppCompatActivity() {

    val layouts = intArrayOf(
            R.layout.welcome_slide1,
            R.layout.welcome_slide2,
            R.layout.welcome_slide3,
            R.layout.welcome_slide4
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFirstRun()

        if(Build.VERSION.SDK_INT >= 21){
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_welcome)

        tab_layout.setupWithViewPager(view_pager)

        view_pager.adapter = MyViewPagerAdapter()
        view_pager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
                // empty
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // empty
            }

            override fun onPageSelected(position: Int) {
                if (position == layouts.size - 1){
                    btn_next.text = "Got it"
                    btn_skip.visibility = View.GONE
                } else{
                    btn_next.text = "Next"
                    btn_skip.visibility = View.VISIBLE
                }
            }
        })

        btn_skip.setOnClickListener {
            launchLoginScreen()
        }

        btn_next.setOnClickListener {
            val current = view_pager.currentItem + 1
            if(current < layouts.size){
                view_pager.currentItem = current
            }else{
                launchLoginScreen()
            }
        }
    }

    private fun isFirstRun(){
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val isFirstRun = prefs.getBoolean("isFirstRun", true)

        if(isFirstRun){
            prefs.edit().putBoolean("isFirstRun", false).apply()
        } else {
            launchLoginScreen()
        }
    }

    private fun launchLoginScreen(){
        startActivity( Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            Log.d(this.javaClass.simpleName, "orientation: landscape")
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.d(this.javaClass.simpleName, "orientation: portrait")
        }
    }

    inner class MyViewPagerAdapter: PagerAdapter(){

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = layoutInflater.inflate(layouts[position], container, false)
            container.addView(view)
            return view
        }

        override fun isViewFromObject(view: View, `object`: Any?): Boolean {
            return view == `object`
        }

        override fun getCount(): Int {
            return layouts.size
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any?) {
            container.removeView( `object` as View)
        }
    }
}
