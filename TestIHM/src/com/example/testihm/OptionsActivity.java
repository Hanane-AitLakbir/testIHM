package com.example.testihm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class OptionsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);

		Button cloud = (Button) findViewById(R.id.cloud);
		//Button webdav = (Button) findViewById(R.id.webdav);

		final LinearLayout ll = (LinearLayout) findViewById(R.id.parentLayout);

		ll.removeAllViews();
		ListView listView = new ListView(getApplicationContext());
		ll.addView(listView);

		//		// Use the current directory as title
		//		String path = "/sdcard";
		//		if (getIntent().hasExtra("path")) {
		//			path = getIntent().getStringExtra("path");
		//		}
		//		setTitle(path);
		//		System.out.println(path);
		//		final String path2 = path;
		//		// Read all files sorted into the values-array
		//		List values = new ArrayList();
		//		File dir = new File(path);
		//		if (!dir.canRead()) {
		//			setTitle(getTitle() + " (inaccessible)");
		//		}
		//		String[] list = dir.list();
		//		if (list != null) {
		//			for (String file : list) {
		//				if (!file.startsWith(".")) {
		//					values.add(file);
		//				}
		//			}
		//		}
		ArrayList values = new ArrayList();
		try {
			FileInputStream input = new FileInputStream("/sdcard/list.json");
			BufferedReader reader = new BufferedReader(new InputStreamReader(input,"iso-8859-1"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			String content = sb.toString();
			input.close();
			System.out.println(content);
			//values.add(content);
			JSONObject json = new JSONObject(content);
			Iterator iterator =json.keys();
			String current;
			HashMap<String, String> map;
			while(iterator.hasNext()){
				map = new HashMap<String, String>();
				current = iterator.next().toString();
				map.put("nameCloud",current);
				map.put("type", json.getString(current));
				//values.add(current+" "+ json.getString(current));
				values.add(map);
//				ListAdapter adapter = new SimpleAdapter(getApplicationContext(), values, R.layout.list_clouds, new String[] {"nameCloud","type"}, new int[] {R.id.nameCloud,R.id.type});
//				listView.setAdapter(adapter);
			}


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(values.toString());

		//Collections.sort(values);

		// Put the data into the list
		//ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_2, android.R.id.text1, values);
		//listView.setAdapter(adapter);
		ListAdapter adapter = new SimpleAdapter(getApplicationContext(), values, R.layout.list_clouds, new String[] {"nameCloud","type"}, new int[] {R.id.nameCloud,R.id.type});
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parents, View view, int position, long id) {
				//WORKING
				String path ="click "+ parents.getItemAtPosition(position).toString();
				Toast toast = Toast.makeText(getApplicationContext(), path, Toast.LENGTH_LONG);
				toast.show();
			}
		});



		cloud.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				Intent intent = new Intent(OptionsActivity.this, AddLocationStorage.class);
				startActivity(intent);
			}
		}
				);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}

}
