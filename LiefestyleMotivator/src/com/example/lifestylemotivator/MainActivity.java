package com.example.lifestylemotivator;

import java.io.IOException;
import java.net.MalformedURLException;

import com.example.liefestylemotivator.R;
import com.example.lifestylemotivator.provider.PlacesProvider;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        PlacesProvider p = new PlacesProvider();
        String result = "";
        try {
        	result = p.printPlaces();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
