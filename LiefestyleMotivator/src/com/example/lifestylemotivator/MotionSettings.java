package com.example.lifestylemotivator;

import com.example.lifestylemotivator.R;
import com.example.lifestylemotivator.provider.AccelLoggerService;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MotionSettings extends Activity  {
	private boolean statusActive = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.md_main);

		Button start = (Button) findViewById(R.id.md_button1);
		
		start.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Start the service repeating periodically
				AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				Intent intent = new Intent(getApplicationContext(), AccelLoggerService.class);
				PendingIntent scheduledIntent = PendingIntent.getService(getApplicationContext(), 
						0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				scheduler.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 
						AlarmManager.INTERVAL_FIFTEEN_MINUTES, scheduledIntent);
				Log.d("AccelLogger", "Starting the service alarm");
				startService(intent);
				statusActive = true;
			}
		});

		Button stop = (Button) findViewById(R.id.md_button2);
		stop.setEnabled(!statusActive);
		stop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Stop the service
				AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				Intent intent = new Intent(getApplicationContext(), AccelLoggerService.class);
				PendingIntent scheduledIntent = PendingIntent.getService(getApplicationContext(), 
						0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				scheduler.cancel(scheduledIntent);
				statusActive = false;
				Log.d("AccelLogger", "Stop the service alarm");
			}
		});
		Button quit = (Button) findViewById(R.id.md_button100);
		quit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent replyIntent = new Intent();
				setResult(RESULT_OK, replyIntent);
				finish();
			}
		});
	}

	

}
