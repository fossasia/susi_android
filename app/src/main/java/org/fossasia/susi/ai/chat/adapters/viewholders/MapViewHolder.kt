package org.fossasia.susi.ai.chat.adapters.viewholders

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotterknife.bindView
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.model.ChatMessage
import org.fossasia.susi.ai.data.model.MapData
import org.fossasia.susi.ai.helper.AndroidHelper
import org.fossasia.susi.ai.helper.MapHelper
import timber.log.Timber

class MapViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val mapImage: ImageView by bindView(R.id.map_image)

    /**
     * Inflate MapView
     *
     * @param model the ChatMessage object
     * @param currContext the Context
     */
    fun setView(model: ChatMessage?, currContext: Context) {

        if (model != null) {
            try {
                val mapHelper = MapHelper(MapData(model.latitude, model.longitude, model.zoom))
                Timber.v(mapHelper.mapURL)

                Picasso.get()
                        .load(mapHelper.mapURL)
                        .into(mapImage, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {}

                            override fun onError(exception: Exception) {
                                Timber.d("map image can't loaded")
                            }
                        })

                mapImage.setOnClickListener {
                    /* Open in Google Maps if installed, otherwise open chrome custom tabs */
                    val mapIntent: Intent
                    if (AndroidHelper.isGoogleMapsInstalled(currContext) && mapHelper.isParseSuccessful) {
                        val gmmIntentUri = Uri.parse(String.format("geo:%s,%s?z=%s", model.latitude, model.longitude, model.zoom))
                        mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        currContext.startActivity(mapIntent)
                        mapIntent.setPackage(AndroidHelper.GOOGLE_MAPS_PKG)
                    } else {
                        val builder = CustomTabsIntent.Builder()
                        val customTabsIntent = builder.build()
                        customTabsIntent.launchUrl(currContext, Uri.parse(mapHelper.webLink)) // launching through custom tabs
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}
