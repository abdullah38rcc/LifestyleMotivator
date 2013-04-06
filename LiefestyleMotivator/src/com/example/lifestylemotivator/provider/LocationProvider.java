package com.example.lifestylemotivator.provider;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationProvider implements LocationListener {
	
    private LocationManager locationManager;
    private Location location;
    private Context context;
    
    public LocationProvider(Context context) {
    	this.context = context;
    	
        locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }
    
    public Location getLocation() {
		return location;
    }
    
    public boolean isGpsEnabled() {
    	return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
	
	@Override
	public void onLocationChanged(Location location) {
		this.location = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}
