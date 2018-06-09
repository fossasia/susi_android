package org.fossasia.susi.ai.chat.adapters.viewholders;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.data.model.ChatMessage;
import org.fossasia.susi.ai.data.model.MapData;
import org.fossasia.susi.ai.helper.AndroidHelper;
import org.fossasia.susi.ai.helper.MapHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * <h1>Map view holder</h1>
 * <p>
 * Created by saurabh on 7/10/16.
 */
public class MapViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.map_image)
    protected ImageView mapImage;

    /**
     * Instantiates a new Map view holder.
     *
     * @param itemView the item view
     */
    public MapViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    /**
     * Inflate MapView
     *
     * @param model       the ChatMessage object
     * @param currContext the Context
     */
    public void setView(final ChatMessage model, final Context currContext) {

        if (model != null) {
            try {
                final MapHelper mapHelper = new MapHelper(new MapData(model.getLatitude(), model.getLongitude(), model.getZoom()));
                Timber.v(mapHelper.getMapURL());

                Picasso.with(currContext.getApplicationContext()).load(mapHelper.getMapURL())
                        .into(mapImage, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                Timber.d("map image can't loaded");
                            }
                        });

                mapImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*
                          Open in Google Maps if installed, otherwise open chrome custom tabs.
                        */
                        Intent mapIntent;
                        if (AndroidHelper.INSTANCE.isGoogleMapsInstalled(currContext) && mapHelper.isParseSuccessful()) {
                            Uri gmmIntentUri = Uri.parse(String.format("geo:%s,%s?z=%s", model.getLatitude(), model.getLongitude(), model.getZoom()));
                            mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            currContext.startActivity(mapIntent);
                            mapIntent.setPackage(AndroidHelper.GOOGLE_MAPS_PKG);
                        } else {
                            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                            CustomTabsIntent customTabsIntent = builder.build();
                            customTabsIntent.launchUrl(currContext, Uri.parse(mapHelper.getWebLink())); //launching through custom tabs
                        }

                    }
                });

            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }
}
