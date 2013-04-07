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
    private Double defaultLatitude;
    private Double defaultLongitude;
    
    public LocationProvider(Context context, Double latitude, Double longitude) {
    	this.context = context;
    	this.defaultLatitude = latitude;
    	this.defaultLongitude = longitude;
        locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
        //FIXME: To save battery need to do this in resume and unregister in pause.
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }
  
    /**
     * Get the current location.
     * If it fails then it retrieves the default NC state campus.
     * @return
     */
    public Location getLocation() {
    	
    	location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	if(location == null) {
    		location = new Location("DEFAULT");
    		location.setLatitude(defaultLatitude);
    		location.setLongitude(defaultLongitude);
    	}
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
