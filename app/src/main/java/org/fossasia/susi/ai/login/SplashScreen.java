package org.fossasia.susi.ai.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import org.fossasia.susi.ai.R;

/**
 * created by Ujjwal Agrawal on 27/02/2018
 */

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread thread=new Thread()
        {
            public void run()
            {
                try
                {
                    sleep(500);
                }
                catch (Exception e){}
                finally {
                    Intent intent=new Intent(SplashScreen.this,WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        thread.start();
    }
}
