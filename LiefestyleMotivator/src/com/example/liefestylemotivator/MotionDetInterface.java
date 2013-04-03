package com.example.liefestylemotivator;

public interface MotionDetInterface {
	/**
	 * Check if the Accelerometer sensor is present.
	 * @return true if sensor present.
	 */
	public boolean isPresent();
	
	/**
	 * Check if the Accelerometer sensor is configured.
	 * @return false if sensor not present or not configured.
	 */
	public boolean isConfigured();
	
	/**
	 * Get current activity level of the use.
	 * @return -1 if not confiured else a number from 0 (no activity)
	 * to 100 (full activity).
	 */
	public static int IDLE = 1;
	public static int LOW_ACTIVITY = 1;
	public static int MEDIUM_ACTIVITY = 2;
	public static int HIGH_ACTIVITY = 3;
	
	public int getActivityLevel();
}
