package wsu.cs558.roadmonitoring.view;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

public class MainActivity extends Activity {

private SensorManager sensorManager;
final String FILE_NAME = "SAMPLEFILE.txt";
final String TEST_STRING = new String("Hello Android");
	
	TextView xCoor; // declare X axis object
	TextView yCoor; // declare Y axis object
	TextView zCoor; // declare Z axis object
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	//	TextView tv = new TextView(this);
			
	}
	
	
}
