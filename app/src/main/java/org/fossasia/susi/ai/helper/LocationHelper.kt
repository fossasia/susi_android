package org.fossasia.susi.ai.helper

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import timber.log.Timber

import java.lang.ref.WeakReference

/**
 * <h1>Helper class to get location of using network and GPS.</h1>

 * Created by chiragw15 on 10/12/16.
 */
class LocationHelper
/**
 * Instantiates a new Location helper.

 * @param context the context
 */
(context: Context) : Service(), LocationListener {

    private var canGetLocation = false
    private val weakContext: WeakReference<Context> = WeakReference(context)

    /**
     * Gets latitude.

     * @return the latitude
     */
    var latitude: Double = 0.toDouble()
        private set
    /**
     * Gets longitude.

     * @return the longitude
     */
    var longitude: Double = 0.toDouble()
        private set
    /**
     * Gets source.

     * @return the source
     */
    var source: String = "network"
        private set

    protected var locationManager: LocationManager? = null

    /**
     * Gets location.
     */
    fun getLocation() {
        val context = weakContext.get()
        var location: Location?
        if (context == null) {
            return
        }
        try {
            locationManager = context
                    .getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (locationManager != null) {
                    locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, (5 * 60 * 1000).toLong(), 10f, this)
                    location = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (location != null) {
                        source = "network"
                        canGetLocation = true
                        latitude = location.latitude
                        longitude = location.longitude
                    }
                }
            }

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (locationManager != null) {
                    locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, (5 * 60 * 1000).toLong(), 10f, this)
                    location = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (location != null) {
                        source = "gps"
                        canGetLocation = true
                        latitude = location.latitude
                        longitude = location.longitude
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    /**
     * Can get location boolean.

     * @return the boolean
     */
    fun canGetLocation(): Boolean {
        return canGetLocation
    }

    /**
     * Remove listener.
     */
    fun removeListener() {
        val mContext = weakContext.get()
        if (mContext != null && locationManager != null) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager?.removeUpdates(this)
            }
        }
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            latitude = location.latitude
            longitude = location.longitude
            source = location.provider
            canGetLocation = true
        }
    }

    override fun onProviderDisabled(provider: String) {
        // This method is intentionally empty
    }

    override fun onProviderEnabled(provider: String) {
        // This method is intentionally empty
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        // This method is intentionally empty
    }

    override fun onBind(arg0: Intent): IBinder? {
        // This method is intentionally empty
        return null
    }
}
