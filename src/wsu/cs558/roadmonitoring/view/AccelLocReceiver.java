package wsu.cs558.roadmonitoring.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import wsu.cs558.roadmonitoring.bean.RoadSeverity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AccelLocReceiver extends BroadcastReceiver {

	private RoadSeverity roadSeverity;

	private static final String ROAD_LATITUDE = "road_latitude";
	private static final String ROAD_LONGITUDE = "road_longitude";
	private static final String ROAD_ID = "road_id";
	private static final String ROAD_SEVERITY = "severity_value";
	List<RoadSeverity> roadSevList;
	GoogleMap googleMap;

	@Override
	public void onReceive(Context arg0, Intent arg1) {

		System.out.println("cmg in AccelLocReceiver");
		Log.i("Alarm receiver", "Came in alarm receiver");
		receiveDataFromServer();

	}

	private void receiveDataFromServer() {
		try {
			roadSevList = new ArrayList<RoadSeverity>();
			HttpClient httpclient = new DefaultHttpClient();

			HttpPost httppost = new HttpPost(
					"http://netlab.encs.vancouver.wsu.edu/web/html/roadseverity.php");

			HttpResponse response = httpclient.execute(httppost);

			String jsonResult = inputStreamToString(
					response.getEntity().getContent()).toString();
			JSONArray jsonArray = new JSONArray(jsonResult);

			for (int i = 0; i < jsonArray.length(); i++) {
				roadSeverity = new RoadSeverity();
				JSONObject object = jsonArray.getJSONObject(i);
				roadSeverity.setRoadId(Integer.parseInt(object
						.getString(ROAD_ID)));
				roadSeverity.setRoadLatitude(Double.parseDouble(object
						.getString(ROAD_LATITUDE)));
				roadSeverity.setRoadLongitude(Double.parseDouble(object
						.getString(ROAD_LONGITUDE)));
				roadSeverity.setRoadSeverity(Double.parseDouble(object
						.getString(ROAD_SEVERITY)));

				roadSevList.add(roadSeverity);

			}
			
			for(int i=0;i<roadSevList.size();i++){
				Log.i("road_id",Integer.toString(roadSevList.get(i).getRoadId()));
				Log.i("road_latitude",Double.toString(roadSevList.get(i).getRoadLatitude()));
				Log.i("road_longitude",Double.toString(roadSevList.get(i).getRoadLongitude()));
				Log.i("road_severity",Double.toString(roadSevList.get(i).getRoadSeverity()));
			}

			// set overlays from above details ***** TBD *************

			
			//googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private StringBuilder inputStreamToString(InputStream is) {
		String rLine = "";
		StringBuilder answer = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		try {
			while ((rLine = rd.readLine()) != null) {
				answer.append(rLine);
			}
		}

		catch (IOException e) {
			e.printStackTrace();
		}
		return answer;
	}

}
