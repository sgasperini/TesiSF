package unibo.progettotesi.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import unibo.progettotesi.R;
import unibo.progettotesi.model.Profile;
import unibo.progettotesi.model.Route;

public class DestinationActivityB extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.destination_activity_b);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		Profile profile = Profile.getProfileFromString(sharedPreferences.getString("CurrentProfile", ""));
		Route route = Route.getRouteFromString(sharedPreferences.getString("CurrentRoute", ""));

		TextView stop = (TextView) findViewById(R.id.endStop_dest);
		TextView meters = (TextView) findViewById(R.id.walking_dest);
		TextView destination = (TextView) findViewById(R.id.destination_dest);

		stop.setText(route.getEndStop().getName());
		//meters
		destination.setText(profile.getEnd().getLocation().getAddress());
	}


}
