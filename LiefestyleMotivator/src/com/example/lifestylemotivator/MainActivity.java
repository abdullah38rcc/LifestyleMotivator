package com.example.lifestylemotivator;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import com.example.lifestylemotivator.R;
import com.example.lifestylemotivator.models.PlaceModel;
import com.example.lifestylemotivator.provider.LocationProvider;
import com.example.lifestylemotivator.provider.PlacesProvider;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {	
	AddStringTask workerTask;
	ArrayList<String> activityList;
	LifestyleRuleEngine ruleEngine = new LifestyleRuleEngine();

	// UI related attributes
	private Button searchBtn;
	private Button cancelBtn;
	private PlacesProvider placeProvider;
	private LocationProvider locationProvider;
	public static final int MENU_PREFS = Menu.FIRST + 1;
	public static final int MENU_DEMO = Menu.FIRST + 2;

	// Weather Service attributes
	CityForecastBO weatherInfo;
	String ZIP;
	private double latitude;
	private double longitude;
	Location location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ZIP = "27560";

		activityList = getActivityListFmXml();

		// Enable search by default and switch off cancel
		searchBtn = (Button) findViewById(R.id.search);
		cancelBtn = (Button)findViewById(R.id.cancel);
		cancelBtn.setEnabled(false);

		placeProvider = new PlacesProvider();
		locationProvider = new LocationProvider(this);

		// Display the result of the search
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				new ArrayList<String>()));


		registerForContextMenu(getListView());
	}

	// -------------------------------------------------------------------------------
	// Menu Handlers
	// -------------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_PREFS, Menu.NONE, "Setting");
		menu.add(Menu.NONE, MENU_DEMO, Menu.NONE, "Demo");

		return(super.onCreateOptionsMenu(menu)); 

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == MENU_DEMO) {
			Intent i = new Intent(MainActivity.this, DemoActivity.class);
			startActivityForResult(i, 0);
			return true;
		}
		else if(item.getItemId() == MENU_PREFS) {
			startActivity(new Intent(MainActivity.this, EditPreferences.class));
			return(true);
		}
		return (super.onOptionsItemSelected(item));
	}

	// -------------------------------------------------------------------------------
	// Button Handlers.
	// Wired to onClick in the xml file.
	// -------------------------------------------------------------------------------
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent i) {
		if(resultCode == RESULT_OK) {
			Toast.makeText(getApplicationContext(), "Running Demo Mode", Toast.LENGTH_SHORT).show();
		}
		else {
			Toast.makeText(getApplicationContext(), "Running Normal Mode", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void searchActivities(View theButton) {
		searchBtn.setEnabled(false);
		EditText editText = (EditText)findViewById(R.id.entry);
		String editTextStr = editText.getText().toString();
		new AddStringTask().execute(editTextStr);
	}
	public void clearActivities(View theButton) {
		cancelBtn.setEnabled(false);
		((ArrayAdapter<String>)getListAdapter()).clear();
		searchBtn.setEnabled(true);
	}


	ArrayList<String> items=new ArrayList<String>();

	// ---------------------------------------------------------------------------------
	// Worker Task.
	// Do not want to block the UI thread while we collect the sensor data.
	// ---------------------------------------------------------------------------------
	class AddStringTask extends AsyncTask<String, String, Void> {
		@Override
		protected Void doInBackground(String... query) {
			items = ruleEngine.getActivites(query[0]);
			for (String item : items) {
				publishProgress(item);
			}

			return(null);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onProgressUpdate(String... item) {
			((ArrayAdapter<String>)getListAdapter()).add(item[0]);
		}

		@Override
		protected void onPreExecute() {
			Log.d("WorkerThread", "Starting with task.");
			searchBtn.setEnabled(false);
		}
		@Override
		protected void onPostExecute(Void unused) {
			cancelBtn.setEnabled(true);
			Log.d("WorkerThread", "Done with task.");
		}
	}

	// ---------------------------------------------------------------------------
	// Our secret formula for using the context to order the list.
	// Implements the set of rules for detecting favorable activities.
	// ---------------------------------------------------------------------------
	// Helper class
	class Pair implements Comparable {
		int score;
		int index;

		public Pair(int s, int i) {
			score = s;
			index = i;
		}

		@Override
		public int compareTo(Object arg0) {
			int s = ((Pair)arg0).score;
			return s > this.score ? -1 : s == this.score ? 0 : 1;
		}

	}
	/**
	 * Load the list of activites from xml file.
	 * @return
	 */
	private ArrayList<String> getActivityListFmXml() {
		ArrayList<String> lst = new ArrayList<String>();
		try {
			InputStream in=getResources().openRawResource(R.raw.activities);
			DocumentBuilder builder=DocumentBuilderFactory
					.newInstance()
					.newDocumentBuilder();
			Document doc=builder.parse(in, null);
			NodeList activities=doc.getElementsByTagName("activity");

			Log.d("NumActivities", Integer.toString(activities.getLength()));

			for (int i=0;i<activities.getLength();i++) {
				Element ele = (Element) activities.item(i);
				//Log.d("Value", ele.getNodeValue());
				//Log.d("Text", activities.item(i).getTextContent());
				//activityList.add(ele.getNodeValue());
				//Log.d("getActivities", "Adding actiivity");
				lst.add(Integer.toString(i) + " hello world ");
			}

			in.close();
		}
		catch (Throwable t) {

		}
		return lst;
	}
	private class LifestyleRuleEngine {
		/**
		 * Returns a  list of activities to do.
		 */
		ArrayList<String> getActivites(String query) {


			Log.d("LifeStyleRuleEngine", "Running get activities");

			// Sort the activity list. Decorate-sort-Undecorate
			Pair pairArr[] = new Pair[activityList.size()];
			for(int i = 0; i < activityList.size(); i++) {
				//TODO compute the score
				int score = i;
				//Store the result.
				pairArr[i] = new Pair(score, i);
			}
			Arrays.sort(pairArr);
			ArrayList<String> sortedList = new ArrayList<String>();
			
			PlaceModel[] places = null;
			try {
				Location location = locationProvider.getLocation();
		    	Double latitude = 35.787149;
		    	Double longitude = -78.681137;
		    	if(location != null) {
		    		latitude = location.getLatitude();
		    		longitude = location.getLongitude();
		    	}
				// leave types an empty string if you do not want to filter by type
				places = placeProvider.getPlaces(latitude.toString(), longitude.toString(), "gym|park|stadium", query);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for(PlaceModel p : places) {
				sortedList.add(p.getName());
			}
//			for(int i = 0; i < activityList.size(); i++) {
//				
//				sortedList.add(activityList.get(pairArr[i].index));
//			}
			return sortedList;  
		}
	}

	// ------------------------------------------------------------------------------------------
	// Weather Service 
	//------------------------------------------------------------------------------------------
	private String getCurrentLocation() {

		LocationManager locationManager;
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

		final int geoLat = (int) (latitude * 1E6);
		final int geoLong = (int) (longitude * 1E6);
		Criteria crit = new Criteria();
		crit.setAccuracy(Criteria.ACCURACY_COARSE);
		String provider = locationManager.getBestProvider(crit, true);
		Location loc = locationManager.getLastKnownLocation(provider);
		latitude = loc.getLatitude();
		longitude = loc.getLongitude();

		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		try {
			List<Address> addresses = geocoder.getFromLocation(geoLat, geoLong, 10);
			if(addresses != null && addresses.size() > 0 ) {
				Address returnedAddress = addresses.get(0);
				StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
				for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
					strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
				}
				System.out.println(strReturnedAddress.toString());
			}

		} catch (IOException e) {
			e.printStackTrace();
		} 

		return null;
	}
	public void setWeatherInfo(CityForecastBO info)
	{
		weatherInfo = info;
	}

	public CityForecastBO getWeatherInfo()
	{

		return weatherInfo;
	}

}

