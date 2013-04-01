package com.example.liefestylemotivator;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
/**
 * Inspired by 
 * http://mobile.tutsplus.com/tutorials/android/android-barometer-logger-acquiring-sensor-data/
 * @author sanjeev
 *
 */
public class AccelLoggerService extends Service implements SensorEventListener {
	private static final String DEBUG_TAG = "AccelLoggerService";
	private SensorManager sensorManager = null;
	private Sensor sensor = null;
	
	private ArrayList<Float> sampleList = new ArrayList<Float>();
	private int minNumSamples = 100;
	private int maxNumSamples = 10240;
	private long startTstamp = 0;
	private long lastTstamp = 0;

	@Override
	public int onStartCommand(Intent intet, int flags, int startId) {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		Log.d(DEBUG_TAG, "Starting service.");
		return START_STICKY;
		
	}
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// Do nothing
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// grab the values and timestamp -- off the main thread
		new SensorEventLoggerTask().execute(event);
		// stop the service
		stopSelf();
		Log.d(DEBUG_TAG, "Stopping service.");
	}
	
	private class SensorEventLoggerTask extends AsyncTask<SensorEvent, Void, Void> {
		
		@Override
		protected Void doInBackground(SensorEvent... arg0) {
			Float f = computeAccel(arg0[0]);
			sampleList.add(f);
			if(sampleList.size() > maxNumSamples) {
				sampleList.remove(0);
			}
			if(startTstamp == 0) {
				startTstamp = arg0[0].timestamp;
				lastTstamp = startTstamp;
			}
			else {
				lastTstamp = arg0[0].timestamp;
			}
			return null;
		}
	}
	private float computeAccel(SensorEvent event) {
		if(sampleList.size() < minNumSamples) {
			return 0;
		}
		else {
			// TODO: Add the code to compute accel
			return 0;
		}
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
