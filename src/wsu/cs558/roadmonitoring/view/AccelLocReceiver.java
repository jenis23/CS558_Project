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

			// set overlays from above details ***** TBD *************

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
