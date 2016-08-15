package unibo.progettotesi.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import unibo.progettotesi.R;
import unibo.progettotesi.model.Route;

public class MainActivity extends AppCompatActivity {
	private String routeS;
	private String stopS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		routeS = sharedPreferences.getString("CurrentRoute", "");
		stopS = sharedPreferences.getString("LastStop", "");
		Log.wtf("CrashDetector route", routeS);
		Log.wtf("CrashDetector stop", stopS);

		if(!routeS.equals("")){
			if(!stopS.equals("")){
				Intent intent = new Intent(this, OnTheGoActivity.class);
				intent.putExtra("LastStop", stopS);
				startActivity(intent);
			}else{
				Intent intent = new Intent(this, BusWaitingActivity.class);
				startActivity(intent);
			}
		}
	}

	public void newProfile(View v){
		Intent intent = new Intent(this, NewProfileActivityB.class);
		intent.putExtra("Start", true);
		startActivity(intent);
	}

	public void newTrip(View v){
		Intent intent = new Intent(this, NewTripActivityB.class);

		startActivity(intent);
	}
}
