package wsu.cs558.roadmonitoring.view;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class Food extends ListActivity {

	String result = null;
	InputStream is = null;
	StringBuilder sb = null;
	JSONArray jArray;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	//	setContentView(R.layout.food);

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		try {

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://netlab.encs.vancouver.wsu.edu/web/html/food.php");
			

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection" + e.toString());
		}

		// convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			sb = new StringBuilder();
			sb.append(reader.readLine() + "\n");
			String line = "0";

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}

			is.close();
			result = sb.toString();

		} catch (Exception e) {
			Log.e("log_tag", "Error converting result " + e.toString());
		}

		// paring data
		int fd_id = 0;
		String fd_name = null;
		try {
			jArray = new JSONArray(result);
			JSONObject json_data = null;

			for (int i = 0; i < jArray.length(); i++) {
				json_data = jArray.getJSONObject(i);
				fd_id = json_data.getInt("FOOD_ID");
				fd_name = json_data.getString("FOOD_NAME");
			}
			System.out.println("Food id:"+fd_id);
			System.out.println("Food name:"+fd_name);
		} catch (JSONException e1) {
			Toast.makeText(getBaseContext(), "No Food Found", Toast.LENGTH_LONG)
					.show();
		}
	}

}