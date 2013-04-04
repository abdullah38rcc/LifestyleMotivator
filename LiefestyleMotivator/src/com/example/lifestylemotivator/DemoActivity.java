package com.example.lifestylemotivator;

import com.example.lifestylemotivator.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DemoActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_main);
	}
	public void demoSet(View button) {
		//TODO Save preferences
		Intent replyIntent = new Intent();
		setResult(RESULT_OK, replyIntent);
		finish();
	}
	public void demoCancel(View button) {
		Intent replyIntent = new Intent();
		setResult(RESULT_CANCELED, replyIntent);
		finish();
	}
}
