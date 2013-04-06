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
 * Accelerometer measure the acceleration force in m/s2 that is applied to the device in all 3 axis.
 * This includes gravity.
 */
public class AccelLoggerService extends Service implements SensorEventListener {
	private static final String DEBUG_TAG = "AccelLoggerService";
	private SensorManager sensorManager = null;
	private Sensor sensor = null;
	private float[] gravity = new float[3];
	private static final float ALPHA = 0.8f;
	private static final int HIGH_PASS_MINIMUM = 10;
	private int highPassCount;
	private static final int THRESHHOLD = 2;
	private static final int RUN_THRESHHOLD = 20;
	
	private MovingAverage sampleWindow = new MovingAverage(1024);
	
	@Override
	public int onStartCommand(Intent intet, int flags, int startId) {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		Log.d(DEBUG_TAG, "Starting service.");
		highPassCount = 0;
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
			double f = computeAccel(arg0[0]);
			if(f < RUN_THRESHHOLD) {
				sampleWindow.pushValue(f);
			}
			
			return null;
		}
	}
	/**
	 * Compute the acceleration from the sensors in m/s2.
	 * @param event
	 * @return
	 */
	private double computeAccel(SensorEvent event) {
		double acceleration = 0;

		float[] values = event.values.clone();
		values = highPass(values[0], values[1], values[2]);
		if (++highPassCount >= HIGH_PASS_MINIMUM) {
			double sumOfSquares = (values[0] * values[0])
					+ (values[1] * values[1])
					+ (values[2] * values[2]);
			acceleration = Math.sqrt(sumOfSquares);


		}
		return acceleration > THRESHHOLD ? acceleration : 0;

	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * Google high pass filter algorithm
	 */
	
	private float[] highPass(float x, float y, float z) {
		float[] filteredValues = new float[3];
		
		gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * x;
		gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * y;
		gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * z;
		
		filteredValues[0] = x - gravity[0];
		filteredValues[1] = y - gravity[1];
		filteredValues[2] = z - gravity[2];
		
		return filteredValues;				
	}
	/**
	 * Filter to detect movement.
	 * @author Google
	 *
	 */
	public class MovingAverage
	{

	    private double circularBuffer[];
	    private double avg;
	    private int circularIndex;
	    private int count;

	    public MovingAverage(int k)
	    {
	        circularBuffer = new double[k];
	        count = 0;
	        circularIndex = 0;
	        avg = 0;
	    }

	    /* Get the current moving average. */
	    public double getValue()
	    {
	        return avg;
	    }

	    public void pushValue(double x)
	    {
	        if (count++ == 0)
	        {
	            primeBuffer(x);
	        }
	        double lastValue = circularBuffer[circularIndex];
	        avg = avg + (x - lastValue) / circularBuffer.length;
	        circularBuffer[circularIndex] = x;
	        circularIndex = nextIndex(circularIndex);
	    }

	    public long getCount()
	    {
	        return count;
	    }

	    private void primeBuffer(double x)
	    {
	        for (int i = 0; i < circularBuffer.length; ++i)
	        {
	            circularBuffer[i] = x;
	        }
	        avg = x;
	    }

	    private int nextIndex(int curIndex)
	    {
	        if (curIndex + 1 >= circularBuffer.length)
	        {
	            return 0;
	        }
	        return curIndex + 1;
	    }
	}

}
