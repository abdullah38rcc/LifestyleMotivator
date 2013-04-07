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
import com.example.lifestylemotivator.models.CityForecastBO;
import com.example.lifestylemotivator.models.LMActivity;
import com.example.lifestylemotivator.models.LMActivityModel;
import com.example.lifestylemotivator.models.LMCurrentCtxt;
import com.example.lifestylemotivator.models.PlaceModel;
import com.example.lifestylemotivator.provider.LocationProvider;
import com.example.lifestylemotivator.provider.PlacesProvider;
import com.example.lifestylemotivator.provider.WeatherProvider;

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
import android.content.res.Resources.NotFoundException;
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
	// Attributes 
	AddStringTask workerTask;
	private LMActivityModel activityModel;
	private ArrayList<LMActivity> activityList;
	private PlacesProvider placeProvider;
	private LocationProvider locationProvider;
	private WeatherProvider weatherProvider;
	
	// UI related attributes
	private Button searchBtn;
	private Button cancelBtn;
	public static final int MENU_PREFS = Menu.FIRST + 1;
	public static final int MENU_DEMO = Menu.FIRST + 2;
	public static final int MENU_SERVICES = Menu.FIRST + 3;
	
	// Default attributes
	// Location based
	private static Double DefaultLatitude = 35.787149;
	private static Double DefaultLongitude = -78.681137; 
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Enable search by default and switch off cancel
		searchBtn = (Button) findViewById(R.id.search);
		cancelBtn = (Button)findViewById(R.id.cancel);
		cancelBtn.setEnabled(false);

		// Display the result of the search
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				new ArrayList<String>()));

		registerForContextMenu(getListView());
		
		// Start the location services
		locationProvider = new LocationProvider(this, DefaultLatitude, DefaultLongitude);
		placeProvider = new PlacesProvider();
		
		// Weather provider
		weatherProvider = new WeatherProvider();
		
		// Load the activities.
		try {
			InputStream in= getResources().openRawResource(R.raw.activities);
			activityModel = new LMActivityModel();
			activityModel.initFmXmlFile(in);
			in.close();
		}
		catch(IOException t) {
			t.printStackTrace();
		}
	}

	// -------------------------------------------------------------------------------
	// Menu Handlers
	// -------------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_PREFS, Menu.NONE, "Setting");
		menu.add(Menu.NONE, MENU_DEMO, Menu.NONE, "Demo");
		menu.add(Menu.NONE, MENU_SERVICES, Menu.NONE, "Services");
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
		else if(item.getItemId() == MENU_SERVICES) {
			Intent i = new Intent(MainActivity.this, MotionSettings.class);
			startActivityForResult(i, 0);
			return true;
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
		// Start the async task.
		new AddStringTask().execute(editTextStr);
	}
	
	public void clearActivities(View theButton) {
		cancelBtn.setEnabled(false);
		((ArrayAdapter<String>)getListAdapter()).clear();
		searchBtn.setEnabled(true);
	}

	// ---------------------------------------------------------------------------------
	// Worker Task.
	// Do not want to block the UI thread while we collect the sensor data.
	// ---------------------------------------------------------------------------------
	class AddStringTask extends AsyncTask<String, String, Void> {
		@Override
		protected Void doInBackground(String... query) {
			// Get the last known location.
			Location currLocation = locationProvider.getLocation();
			
			// Get the places from the last known location.
			PlaceModel[] placesArr = null;
			try {
				placesArr = placeProvider.getPlaces(String.valueOf(currLocation.getLatitude()),
												 				  String.valueOf(currLocation.getLongitude()),
												 				  "gym|park|stadium", query[0]);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			// Get the current weather
			CityForecastBO forecast = null;
			try {
				forecast = weatherProvider.getWeatherForecast(locationProvider.getPostalCode());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Fill the current context.
			LMCurrentCtxt ctxt = new LMCurrentCtxt();
			if(placesArr != null && placesArr.length > 0) {
				ctxt.facilityName = placesArr[0].getName();
				ctxt.isFacilityValid = true;
			}
			if(forecast != null) {
				ctxt.isTempratureValid = true;
				ctxt.temprature = forecast.getTemprature();
			}
			// Call the model to fill the activityList.
			
			activityList = activityModel.sortActivityLst(ctxt);
			return null;
			
		}

		@Override
		protected void onPreExecute() {
			Log.d("WorkerThread", "Starting with task.");
			searchBtn.setEnabled(false);
		}
		@Override
		protected void onPostExecute(Void unused) {
			for(int i = 0; i < activityList.size(); i++) {
				((ArrayAdapter)getListAdapter()).add(activityList.get(i).getValue());
			}
			cancelBtn.setEnabled(true);
			Log.d("WorkerThread", "Done with task.");
		}
	}

}

