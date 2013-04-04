package com.example.lifestylemotivator;

import com.example.lifestylemotivator.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class DemoActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_main);
	}
	public void demoSet(View button) {
		// -----------------------------------------------------------
		// Store Preferences.
		// ------------------------------------------------------------
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("DemoMode", true);
		
		EditText text1;
		text1 = (EditText) findViewById(R.id.accel_entry);
		float accel_entry;
		try {
			accel_entry = Float.parseFloat(text1.getText().toString());
			
		} catch(NumberFormatException nfe) {
			Log.d("FloatException", nfe.getMessage());
			accel_entry = 0;
		}
		editor.putFloat("AccelValue", accel_entry);
		// Must commit for the changes to be persistent.
		editor.commit();
		
		// Reply back to the main activity
		Intent replyIntent = new Intent();
		setResult(RESULT_OK, replyIntent);
		finish();
	}
	public void demoCancel(View button) {
		//Set the demo mode to false 
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("DemoMode", false);
		editor.commit();
		
		Intent replyIntent = new Intent();
		setResult(RESULT_CANCELED, replyIntent);
		finish();
	}
}
