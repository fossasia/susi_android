package org.fossasia.susi.ai.chat.adapters.viewholders

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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

                Glide.with(currContext)
                        .load(mapHelper.mapURL)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                Timber.d("map image can't loaded")
                                return true
                            }
                            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean) = true
                        })
                        .into(mapImage)

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
