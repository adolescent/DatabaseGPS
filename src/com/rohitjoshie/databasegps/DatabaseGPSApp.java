package com.rohitjoshie.databasegps;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

public class DatabaseGPSApp extends Activity implements OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private EditText editTextShowLocation;
	private EditText editTextPhoneNo;
	private Button onButton;
	private Button offButton;
	private ProgressBar progress;

	private LocationManager locManager;
	private LocationListener locListener = new MyLocationListener();

	private boolean gps_enabled = false;
	private boolean network_enabled = false;

	private MyDatabaseHelper databaseHelper;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hello_android_gps);
		editTextShowLocation = (EditText) findViewById(R.id.editTextShowLocation);
		editTextPhoneNo = (EditText) findViewById(R.id.editTextPhoneNo);

		progress = (ProgressBar) findViewById(R.id.progressBar1);
		progress.setVisibility(View.GONE);

		onButton = (Button) findViewById(R.id.onButton);
		onButton.setOnClickListener(this);

		offButton = (Button) findViewById(R.id.offButton);
		offButton.setOnClickListener(this);
		
		locManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		databaseHelper = new MyDatabaseHelper(this);
		editTextPhoneNo.setText(getPhoneNo());

	}

	public void onSaveData(View v) {
		databaseHelper.addContact("Somebody",
				Integer.parseInt(editTextPhoneNo.getText().toString()));
		Toast.makeText(getApplicationContext(),
				"Phone No. Saved in database :" + editTextPhoneNo.getText(),
				Toast.LENGTH_LONG).show();
	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.onButton: {
			progress.setVisibility(View.VISIBLE);
			// exceptions will be thrown if provider is not permitted.
			try {
				//Get gps enabled status
				gps_enabled = locManager
						.isProviderEnabled(LocationManager.GPS_PROVIDER);
			} catch (Exception ex) {
			}
			try {
				// network_enabled =
				// locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			} catch (Exception ex) {
			}

			// don't start listeners if no provider is enabled
			if (!gps_enabled && !network_enabled) {
				AlertDialog.Builder builder = new Builder(this);
				builder.setTitle("Attention!");
				builder.setMessage("Sorry, location is not determined. Please enable gps detection");
				builder.setPositiveButton("OK", this);
				builder.setNeutralButton("Cancel", this);
				builder.create().show();
				progress.setVisibility(View.GONE);
			}

			if (gps_enabled) {
				//For 1 minute checking and 50m distance change update
				locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						60 * 1000, 50, locListener);
				
				//show a toast message
				Toast.makeText(getApplicationContext(),
						"Location monitoring is Enabled.", Toast.LENGTH_LONG).show();
			}
			
			if (network_enabled) {
				// locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				// 0, 0, locListener);
			}
			break;
		}
		case R.id.offButton: {
			progress.setVisibility(View.GONE);
			locManager.removeUpdates(locListener);
			Toast.makeText(getApplicationContext(),
					"Location Monitoring is turned off.", Toast.LENGTH_LONG).show();
			break;
			}
		}

	}

	/*
	 * @Override public void onClick(View v) {
	 * 
	 * }
	 */

	class MyLocationListener implements LocationListener {
		
		//Called when location is changed
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				// This needs to stop getting the location data and save the
				// battery power.
				// locManager.removeUpdates(locListener);

				String longitude = "Londitude: " + location.getLongitude();
				String latitude = "Latitude: " + location.getLatitude();
				String altitiude = "Altitiude: " + location.getAltitude();
				String accuracy = "Accuracy: " + location.getAccuracy();
				String time = "Time: " + location.getTime();

				editTextShowLocation.setText(longitude + "\n" + latitude + "\n"
						+ altitiude + "\n" + accuracy + "\n" + time);
				progress.setVisibility(View.GONE);

				sendSMS(longitude + "\n " + latitude);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	}

	public String getPhoneNo() {
		int phone = 0;

		Cursor AllContacts = databaseHelper.getContacts();

		AllContacts.moveToFirst();
		while (!AllContacts.isAfterLast()) {
			// String Name = AllContacts.getString(1);
			phone = AllContacts.getInt(2);
			AllContacts.moveToNext();
		}
		AllContacts.close();
		return Integer.toString(phone);
	}

	//Sends sms
	public void sendSMS(String msgBody) {
		String phoneNumber = getPhoneNo();
		String message = msgBody;

		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(phoneNumber, null, msgBody, null, null);
		Toast.makeText(getApplicationContext(),
				"Message Sent to " + phoneNumber, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_NEUTRAL) {
			editTextShowLocation
					.setText("Sorry, location is not determined. To fix this please enable location providers");
		} else if (which == DialogInterface.BUTTON_POSITIVE) {
			startActivity(new Intent(
					android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}
	}

}
