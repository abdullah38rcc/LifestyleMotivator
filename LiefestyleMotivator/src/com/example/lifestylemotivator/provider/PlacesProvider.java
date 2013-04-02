package com.example.lifestylemotivator.provider;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.example.lifestylemotivator.models.PlaceModel;

public class PlacesProvider {

	public PlaceModel[] getPlaces() throws MalformedURLException, IOException {
		String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
		String charset = "UTF-8";
		String location = URLEncoder.encode("-33.8670522,151.1957362", charset);
		String radius = URLEncoder.encode("500", charset);
		String types = URLEncoder.encode("food", charset);
		String name = URLEncoder.encode("harbour", charset);
		String sensor = URLEncoder.encode("false", charset);
		String key = URLEncoder.encode("AIzaSyCh_LKVI3HSiAhVVcyMpz7EXAGrgIvRhIY", charset);
		
		String query = String.format("location=%s&radius=%s&types=%s&name=%s&sensor=%s&key=%s", location, radius, types, name, sensor, key);

		url = url + query;
		HttpsURLConnection urlConnection = (HttpsURLConnection)new URL(url).openConnection();

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
		PlaceModel[] places = new PlaceModel[results.size()];
		int i = 0;
		for(Object o : results) {
			JSONObject place = (JSONObject) o;
			places[i] = new PlaceModel();
			places[i].setName((String) place.get("name"));
			JSONArray placeTypes = (JSONArray) place.get("types");
			String[] typesArray = (String[]) placeTypes.toArray(new String[placeTypes.size()]);
			places[i].setType(typesArray);
			JSONObject geometry = (JSONObject) place.get("geometry");
			JSONObject loc = (JSONObject) geometry.get("location");
			places[i].setLatitude(loc.get("lat").toString());
			places[i].setLongitude(loc.get("lng").toString());
			i = i + 1;
		}
		
		return places;
	}
}
