package org.fossasia.susi.ai

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import org.fossasia.susi.ai.login.WelcomeActivity

/**
 * Created by saurav on 28/2/18.
 */
class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler().postDelayed(Runnable {
            val i = Intent(this@SplashScreenActivity, WelcomeActivity::class.java)
            startActivity(i)
            finish()
        }, SPLASH_TIMER.toLong())
    }

    companion object {

        private val SPLASH_TIMER = 1000
    }
}