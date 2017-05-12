package org.fossasia.susi.ai.rest.model;

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

/**
 * Created by chiragw15 on 10/12/16.
 */

public class LocationHelper extends Service implements LocationListener {

    private final Context mContext;

    private boolean canGetLocation = false;

    private Location location;
    private float latitude;
    private float longitude;
    private String source;

    protected LocationManager locationManager;

    public LocationHelper(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled || isNetworkEnabled) {
                this.canGetLocation = true;
                if (isNetworkEnabled && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = (float) location.getLatitude();
                            longitude = (float) location.getLongitude();
                            source = "network";
                        }
                    }
                }

                if (isGPSEnabled && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = (float) location.getLatitude();
                            longitude = (float) location.getLongitude();
                            source = "gps";
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public float getLatitude() {
        if (location != null) {
            latitude = (float) location.getLatitude();
        }
        return latitude;
    }

    public float getLongitude() {
        if (location != null) {
            longitude = (float) location.getLongitude();
        }

        return longitude;
    }

    public String getSource() {
        return source;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        // This method is intentionally empty
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
