package com.example.lifestylemotivator;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.example.lifestylemotivator.R;
import com.example.lifestylemotivator.provider.PlacesProvider;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	private Button searchBtn;
	private Button cancelBtn;
	AddStringTask workerTask;
	public static final int MENU_DEMO = Menu.FIRST + 1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        searchBtn = (Button) findViewById(R.id.search);
        cancelBtn = (Button)findViewById(R.id.cancel);
        
        cancelBtn.setEnabled(false);
        
        PlacesProvider p = new PlacesProvider();
        
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                new ArrayList<String>()));
        workerTask = new AddStringTask();
        
        registerForContextMenu(getListView());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
    	return (super.onOptionsItemSelected(item));
    }
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
    	  workerTask.execute();
    }
    public void clearActivities(View theButton) {
    	cancelBtn.setEnabled(false);
    	((ArrayAdapter<String>)getListAdapter()).clear();
    	searchBtn.setEnabled(true);
    }
    
    
    ArrayList<String> items=new ArrayList<String>();
    
    
    class AddStringTask extends AsyncTask<Void, String, Void> {
    	@Override
    	protected Void doInBackground(Void... unused) {
    		try {
    			InputStream in=getResources().openRawResource(R.raw.activities);
    			DocumentBuilder builder=DocumentBuilderFactory
    					.newInstance()
    					.newDocumentBuilder();
    			Document doc=builder.parse(in, null);
    			NodeList activities=doc.getElementsByTagName("activity");

    			for (int i=0;i<activities.getLength();i++) {
    				items.add(((Element)activities.item(i)).getAttribute("value"));
    			}

    			in.close();
    		}
    		catch (Throwable t) {
    		
    		}

    		for (String item : items) {
    			publishProgress(item);
    			SystemClock.sleep(200);
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
        	searchBtn.setClickable(false);
        }
        @Override
        protected void onPostExecute(Void unused) {
        	cancelBtn.setEnabled(true);
        }
      }
}
