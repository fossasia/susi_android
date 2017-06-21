package org.fossasia.susi.ai.helper;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import java.lang.ref.WeakReference;

/**
 * <h1>Helper class to get location of using network and GPS.</h1>
 *
 * Created by chiragw15 on 10/12/16.
 */
public class LocationHelper extends Service implements LocationListener {

    private boolean canGetLocation = false;
    private final WeakReference<Context> weakContext;

    private double latitude;
    private double longitude;
    private String source;

    protected LocationManager locationManager;

    /**
     * Instantiates a new Location helper.
     *
     * @param context the context
     */
    public LocationHelper(Context context) {
        weakContext = new WeakReference<Context>(context);
    }

    /**
     * Gets location.
     */
    public void getLocation() {
        Context mContext = weakContext.get();
        Location location;
        if (mContext == null) {
            return;
        }
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (locationManager != null) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5 * 60 * 1000, 10, this);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        source = "network";
                        canGetLocation = true;
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (locationManager != null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 60 * 1000, 10, this);
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        source = "gps";
                        canGetLocation = true;
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets latitude.
     *
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets longitude.
     *
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Gets source.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * Can get location boolean.
     *
     * @return the boolean
     */
    public boolean canGetLocation() {
        return canGetLocation;
    }

    /**
     * Remove listener.
     */
    public void removeListener() {
        Context mContext = weakContext.get();
        if (mContext != null && locationManager != null) {
            if (ActivityCompat.checkSelfPermission(mContext,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(mContext,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.removeUpdates(this);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            source = location.getProvider();
            canGetLocation = true;
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // This method is intentionally empty
    }

    @Override
    public void onProviderEnabled(String provider) {
        // This method is intentionally empty
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // This method is intentionally empty
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // This method is intentionally empty
        return null;
    }
}
