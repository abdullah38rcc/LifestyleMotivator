package com.example.lifestylemotivator.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.example.lifestylemotivator.models.PlaceModel;

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
    
    public String getPostalCode() throws MalformedURLException, IOException {
    	String url = "http://maps.googleapis.com/maps/api/geocode/json?";
		String charset = "UTF-8";
		Location loc = this.getLocation();
		String location = URLEncoder.encode(loc.getLatitude() + "," + loc.getLongitude(), charset);
		String query = String.format("latlng=%s&sensor=true", location);

		url = url + query;
		HttpURLConnection urlConnection = (HttpURLConnection)new URL(url).openConnection();

		InputStream result = urlConnection.getInputStream();
	    java.util.Scanner s = new java.util.Scanner(result).useDelimiter("\\A");
	    String response = s.hasNext() ? s.next() : "";
	    
	    JSONObject json = null;
	    
	    try {
			json = (JSONObject)new JSONParser().parse(response);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    JSONArray results = (JSONArray) json.get("results");
	    JSONObject firstMatch = (JSONObject) results.get(0);
	    
		JSONArray components = (JSONArray) firstMatch.get("address_components");
		String postalCode = null;
		int i = 0;
		for(Object o : components) {
			JSONObject object = (JSONObject) o;
			JSONArray types = (JSONArray) object.get("types");
			for(Object t : types) {
				String type = (String) t;
				if(type.equals("postal_code")) {
					return (String) object.get("long_name");
				}
			}
		}
		
		
		return postalCode;
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
