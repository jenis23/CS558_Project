package wsu.cs558.roadmonitoring.view;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import wsu.cs558.roadmonitoring.bean.AccelLocData;
import wsu.cs558.roadmonitoring.helper.DatabaseHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AccelLocSender extends BroadcastReceiver {

	private DatabaseHelper databaseHelper;

	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub

		System.out.println("cmg in AccelLocsender");
		Log.i("Alarm sender", "Came in alarm sender");

		databaseHelper = new DatabaseHelper(context);
		
		sendDataToServer();

	}

	private void sendDataToServer() {

		try {
			int locDataCount = databaseHelper.getLocDataCount();
			List<AccelLocData> accelLocDataList = databaseHelper.getAllData();

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(
					"http://netlab.encs.vancouver.wsu.edu/web/html/accelLocData.php");

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			Log.d("datacount", Integer.toString(locDataCount));
			nameValuePairs.add(new BasicNameValuePair("datacount", Integer
					.toString(locDataCount)));
			for (int i = 0; i < accelLocDataList.size(); i++) {

				nameValuePairs
						.add(new BasicNameValuePair("latitude" + i,
								Double.toString(accelLocDataList.get(i)
										.getLatitude())));
				nameValuePairs
						.add(new BasicNameValuePair("longitude" + i, Double
								.toString(accelLocDataList.get(i)
										.getLongitude())));
				nameValuePairs.add(new BasicNameValuePair("accelX" + i, Double
						.toString(accelLocDataList.get(i).getX())));
				nameValuePairs.add(new BasicNameValuePair("accelY" + i, Double
						.toString(accelLocDataList.get(i).getY())));
				nameValuePairs.add(new BasicNameValuePair("accelZ" + i, Double
						.toString(accelLocDataList.get(i).getZ())));
				nameValuePairs
						.add(new BasicNameValuePair("timeStamp" + i, Double
								.toString(accelLocDataList.get(i)
										.getTimeStamp())));

			}

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			httpClient.execute(httpPost);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
