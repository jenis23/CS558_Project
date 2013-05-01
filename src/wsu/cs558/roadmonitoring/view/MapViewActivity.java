package wsu.cs558.roadmonitoring.view;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import wsu.cs558.roadmonitoring.bean.AccelLocData;
import wsu.cs558.roadmonitoring.helper.DatabaseHelper;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

public class MapViewActivity extends Activity implements LocationListener,
		SensorEventListener, OnClickListener {

	GoogleMap googleMap;

	private boolean started = false;
	private ArrayList<AccelLocData> sensorData;
	private SensorManager sensorManager;
	private Button btnStart, btnStop;
	private String provider;

	Marker now;
	// File root, dir, sensorFile;
	FileOutputStream fOut;
	private Sensor mAccelerometer;
	private FileWriter writer;
	private DatabaseHelper databaseHelper;
	private BroadcastReceiver alarmReceiver;
	private PendingIntent pendingIntentSender, pendingIntentReceiver;

	private AlarmManager alarmManager;
	private Intent alarmIntent,alarmIntent2;
	private SQLiteDatabase db1;
	// private Button btnUpload;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try {
			db1 = openOrCreateDatabase("roadmonitor.db",
							SQLiteDatabase.CREATE_IF_NECESSARY, null);
			db1.execSQL("DROP TABLE IF EXISTS AccelLocation");
			db1.close();
			databaseHelper = new DatabaseHelper(this);
			databaseHelper.removeAll();
			
			
			Log.v("datacount",
					Integer.toString(databaseHelper.getLocDataCount()));

			sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			mAccelerometer = sensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

			btnStart = (Button) findViewById(R.id.btnStart);
			btnStop = (Button) findViewById(R.id.btnStop);
			btnStart.setOnClickListener(this);
			btnStop.setOnClickListener(this);
			btnStart.setEnabled(true);
			btnStop.setEnabled(false);

			alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

			int status = GooglePlayServicesUtil
					.isGooglePlayServicesAvailable(getBaseContext());
			if (status != ConnectionResult.SUCCESS) { // Google Play Services
														// are
														// not available

				int requestCode = 10;
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status,
						this, requestCode);
				dialog.show();

			} else { // Google Play Services are available

				// Getting reference to the SupportMapFragment of
				// activity_main.xml
				// SupportMapFragment supportMapFragment = (MapFragment)
				// getFragmentManager().findFragmentById(R.id.map);

				// Getting GoogleMap object from the fragment
				googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
				 

				// can use for overlay on the map
				/*List<Double> latList = new ArrayList<Double>();
				latList.add(45.7309593);
				latList.add(46.34);
				latList.add(47.34);

				List<Double> lonList = new ArrayList<Double>();
				lonList.add(-122.6365384);
				lonList.add(-123.6365384);
				lonList.add(-124.6365384);

				for (int i = 0; i < 3; i++) {
					// LatLng latLng = new LatLng(45.7309593, -122.6365384);
					LatLng latLng = new LatLng(latList.get(i).doubleValue(),
							lonList.get(i).doubleValue());
					googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
					googleMap
							.addMarker(new MarkerOptions()
									.position(latLng)
									.title("My Spot")
									.snippet("This is my spot!")
									.icon(BitmapDescriptorFactory
											.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
				}*/
			/*	googleMap.setOnMapClickListener(new OnMapClickListener() {

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
*/
				// Enabling MyLocation Layer of Google Map
				googleMap.setMyLocationEnabled(true);

				LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

				Criteria criteria = new Criteria();
				provider = locationManager.getBestProvider(criteria, true);
				Location location = locationManager
						.getLastKnownLocation(provider);

				if (location != null) {
					onLocationChanged(location);
				}

				locationManager
						.requestLocationUpdates(provider, 60000, 0, this);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
			AccelLocData accelLocData = new AccelLocData(timestamp, x, y, z,
					latitude, longitude);

			// Log.d("X data","data x:" + data.getX());

			try {
				// writer.write(accelLocData.toString());
				 if (databaseHelper != null)
				 databaseHelper.insertLocData(accelLocData);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onLocationChanged(Location location) {

//		TextView tvLocation = (TextView) findViewById(R.id.tv_location);
		
		/*if(now != null){
            now.remove();

        }*/


		// Getting latitude of the current location
		double latitude = location.getLatitude();

		// Getting longitude of the current location
		double longitude = location.getLongitude();

		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);

		
	//	now = googleMap.addMarker(new MarkerOptions().position(latLng));
		
		
		// Showing the current location in Google Map
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

		// Zoom in the Google Map
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
		
		// TBD : Try to add Overlay here
		


	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnStart:

			Context context = getApplicationContext();
			
			alarmIntent = new Intent(context, AccelLocSender.class);
			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			pendingIntentSender = PendingIntent.getBroadcast(context, 0,
					alarmIntent, 0); 

			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					System.currentTimeMillis(), 30000, pendingIntentSender); 

			alarmIntent2 = new Intent(context, AccelLocReceiver.class);
			pendingIntentReceiver = PendingIntent.getBroadcast(context, 0,
					alarmIntent2, 0);
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					System.currentTimeMillis(), 30000, pendingIntentReceiver);

			btnStart.setEnabled(false);
			btnStop.setEnabled(true);
			Log.d("startbutton", "came on click of start");
			started = true;

			// delete all files..
			// start thread to send data

			//sensorManager.registerListener(this, mAccelerometer,SensorManager.SENSOR_DELAY_FASTEST);
			sensorManager.registerListener(this, mAccelerometer,SensorManager.SENSOR_DELAY_UI);
			break;
		case R.id.btnStop:
			try {
				btnStart.setEnabled(true);
				btnStop.setEnabled(false);
				// btnUpload.setEnabled(true);
				started = false;

				sensorManager.unregisterListener(this);

				Context context1 = getApplicationContext();
				AlarmManager alarmManager1 = (AlarmManager) context1
						.getSystemService(Context.ALARM_SERVICE);
				alarmManager1.cancel(pendingIntentSender);
				alarmManager1.cancel(pendingIntentReceiver);

			//	System.exit(0);
				//List<AccelLocData> accelLocDataList = databaseHelper.getAllData();
				
				
				
				/*HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(
						"http://netlab.encs.vancouver.wsu.edu/web/html/accelLocData2.php");
				Gson gson = new Gson();
				Log.i("json data:",gson.toJson(accelLocDataList));
				
				
				String jsonString = gson.toJson(accelLocDataList);
				StringEntity stringEntity = new StringEntity(jsonString);
		
				httpPost.setEntity(stringEntity);
				
				
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-type", "application/json");
				httpPost.setHeader("jsondata",jsonString);
				
				
				httpClient.execute(httpPost);*/
				
				/*
				int locDataCount = databaseHelper.getLocDataCount();

				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(
						"http://netlab.encs.vancouver.wsu.edu/web/html/accelLocData.php");

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				Log.d("datacount", Integer.toString(locDataCount));
				nameValuePairs.add(new BasicNameValuePair("datacount", Integer
						.toString(locDataCount)));
				for (int i = 0; i < accelLocDataList.size(); i++) {

					nameValuePairs.add(new BasicNameValuePair("latitude" + i,
							Double.toString(accelLocDataList.get(i)
									.getLatitude())));
					nameValuePairs.add(new BasicNameValuePair("longitude" + i,
							Double.toString(accelLocDataList.get(i)
									.getLongitude())));
					nameValuePairs.add(new BasicNameValuePair("accelX" + i,
							Double.toString(accelLocDataList.get(i).getX())));
					nameValuePairs.add(new BasicNameValuePair("accelY" + i,
							Double.toString(accelLocDataList.get(i).getY())));
					nameValuePairs.add(new BasicNameValuePair("accelZ" + i,
							Double.toString(accelLocDataList.get(i).getZ())));
					nameValuePairs.add(new BasicNameValuePair("timeStamp" + i,
							Double.toString(accelLocDataList.get(i)
									.getTimeStamp())));

				}
				

				
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				httpClient.execute(httpPost);*/


				/*
				 * if(writer != null) { try { writer.close(); } catch
				 * (IOException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); } }
				 */
				} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

	}

	protected void onPause() {
		super.onPause();

		/*
		 * if (writer != null) { try { writer.close(); } catch (IOException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); } }
		 */
	}

	protected void onResume() {
		super.onResume();
		/*
		 * try { Log.d("onresume","called onresume"); writer = new
		 * FileWriter(sensorFile, true); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
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
