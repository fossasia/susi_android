package org.fossasia.susi.ai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.rest.BaseUrl;
import org.fossasia.susi.ai.rest.ClientBuilder;
import org.fossasia.susi.ai.rest.model.SusiBaseUrls;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jigyasa on 21/08/16.
 */

public class SplashScreen extends AppCompatActivity {

    @BindView(R.id.splash_image)
    ImageView imageView;

    private ClientBuilder clientBuilder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        clientBuilder = new ClientBuilder();
        ButterKnife.bind(this);
        Glide.with(this).load(R.drawable.susi_image).into(imageView);
        clientBuilder.getSusiApi().getSusiBaseUrls().enqueue(
                new Callback<SusiBaseUrls>() {
                    @Override
                    public void onResponse(Call<SusiBaseUrls> call,
                            Response<SusiBaseUrls> response) {
                        SusiBaseUrls baseUrls = response.body();
                        PrefManager.saveBaseUrls(baseUrls);
                        PrefManager.setSusiRunningBaseUrl(BaseUrl.PROTOCOL_HTTP +
                                baseUrls.getSusiServices().get(0));
                        ClientBuilder.createSusiService();
                        startMainActivity();
                    }

                    @Override
                    public void onFailure(Call<SusiBaseUrls> call, Throwable t) {
                        startMainActivity();
                    }
                });
    }

    private void startMainActivity() {
        Intent intent;
        if (!PrefManager.hasTokenExpired()) {
            intent = new Intent(SplashScreen.this, MainActivity.class);
        } else {
            intent = new Intent(SplashScreen.this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}