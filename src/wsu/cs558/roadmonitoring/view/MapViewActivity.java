package wsu.cs558.roadmonitoring.view;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import wsu.cs558.roadmonitoring.bean.AccelLocData;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapViewActivity extends Activity implements LocationListener,
		SensorEventListener, OnClickListener {

	GoogleMap googleMap;

	private boolean started = false;
	private ArrayList<AccelLocData> sensorData;
	private SensorManager sensorManager;
	private Button btnStart, btnStop;
	private String provider;

	// private Button btnUpload;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorData = new ArrayList<AccelLocData>();
		btnStart = (Button) findViewById(R.id.btnStart);
		btnStop = (Button) findViewById(R.id.btnStop);
		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		btnStart.setEnabled(true);
		btnStop.setEnabled(false);

		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());
		if (status != ConnectionResult.SUCCESS) { // Google Play Services are
													// not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();

		} else { // Google Play Services are available

			// Getting reference to the SupportMapFragment of activity_main.xml
			// SupportMapFragment supportMapFragment = (MapFragment)
			// getFragmentManager().findFragmentById(R.id.map);

			// Getting GoogleMap object from the fragment
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			// googleMap = supportMapFragment.getMap();

			List<Double> latList = new ArrayList<Double>();
			latList.add(45.7309593);
			latList.add(46.34);
			latList.add(47.34);

			List<Double> lonList = new ArrayList<Double>();
			lonList.add(-122.6365384);
			lonList.add(-123.6365384);
			lonList.add(-124.6365384);

			for (int i = 0; i < 3; i++) {
			//	LatLng latLng = new LatLng(45.7309593, -122.6365384);
				LatLng latLng = new LatLng(latList.get(i).doubleValue(), lonList.get(i).doubleValue());
				googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				googleMap
						.addMarker(new MarkerOptions()
								.position(latLng)
								.title("My Spot")
								.snippet("This is my spot!")
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
			}
			googleMap.setOnMapClickListener(new OnMapClickListener() {

				@Override
				public void onMapClick(LatLng latLng) {

					// Creating a marker
					MarkerOptions markerOptions = new MarkerOptions();

					// Setting the position for the marker
					markerOptions.position(latLng);

					// Setting the title for the marker.
					// This will be displayed on taping the marker
					markerOptions.title(latLng.latitude + " : "
							+ latLng.longitude);

					// Clears the previously touched position
					googleMap.clear();

					// Animating to the touched position
					googleMap.animateCamera(CameraUpdateFactory
							.newLatLng(latLng));

					// Placing a marker on the touched position
					googleMap.addMarker(markerOptions);
				}
			});

			// Enabling MyLocation Layer of Google Map
			googleMap.setMyLocationEnabled(true);

			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			Criteria criteria = new Criteria();
			provider = locationManager.getBestProvider(criteria, true);
			Location location = locationManager.getLastKnownLocation(provider);

			if (location != null) {
				onLocationChanged(location);
			}

			locationManager.requestLocationUpdates(provider, 20000, 0, this);
		}

	}

	public void onSensorChanged(SensorEvent event) {

		if (started) {

			double x = event.values[0];
			double y = event.values[1];
			double z = event.values[2];

			long timestamp = System.currentTimeMillis();

			LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
			criteria.setAccuracy(Criteria.ACCURACY_FINE);

			provider = locManager.getBestProvider(criteria, true);
			Location location = locManager.getLastKnownLocation(provider);

			double latitude = 0;
			double longitude = 0;
			if (location != null) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			}
			AccelLocData data = new AccelLocData(timestamp, x, y, z, latitude,
					longitude);

			 /*System.out.println("Accel Data:" + data.toString());
			 System.out.println("Latitude:" + latitude);
			 System.out.println("Longitude:" + longitude);*/

			sensorData.add(data);

		}
	}

	@Override
	public void onLocationChanged(Location location) {
/*
		TextView tvLocation = (TextView) findViewById(R.id.tv_location);

		// Getting latitude of the current location
		double latitude = location.getLatitude();

		// Getting longitude of the current location
		double longitude = location.getLongitude();

		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);

		// Showing the current location in Google Map
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

		// Zoom in the Google Map
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
		System.out.println("Latitude:" + latitude + ", Longitude:" + longitude);

		// Setting latitude and longitude in the TextView tv_location
		// tvLocation.setText("Latitude:" + latitude + ", Longitude:" +
		// longitude);
		 
		 */
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnStart:
			btnStart.setEnabled(false);
			btnStop.setEnabled(true);
			// btnUpload.setEnabled(false);
		//	sensorData = new ArrayList<AccelLocData>();
			// save prev data if available
			started = true;
			try {
				File root = android.os.Environment
						.getExternalStorageDirectory();
				File dir = new File(root.getAbsolutePath() + "/roadmonitor");
				dir.mkdirs();
				File sensorFile = new File(dir, "acc.txt");

				 sensorFile.createNewFile();
				FileOutputStream fOut = new FileOutputStream(sensorFile);
				ObjectOutputStream myOutWriter = new ObjectOutputStream(fOut);
				System.out.println("Sensor data size:"+sensorData.size());
				
				
				
				myOutWriter.writeObject(sensorData);
				myOutWriter.close();
				fOut.close();
			} catch (Exception e) {

			}
			Sensor accel = sensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			sensorManager.registerListener(this, accel,
					SensorManager.SENSOR_DELAY_FASTEST);
			break;
		case R.id.btnStop:
			btnStart.setEnabled(true);
			btnStop.setEnabled(false);
			// btnUpload.setEnabled(true);
			started = false;
			sensorManager.unregisterListener(this);
			break;
		default:
			break;
		}

	}
	@Override
	public void onProviderDisabled(String arg0) {
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

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

}
