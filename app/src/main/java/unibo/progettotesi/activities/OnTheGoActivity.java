package unibo.progettotesi.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import unibo.progettotesi.R;
import unibo.progettotesi.model.Leg;
import unibo.progettotesi.model.Route;
import unibo.progettotesi.utilities.RealTimeTracker;

public class OnTheGoActivity extends Activity {
	private Route route;
	private Leg currentLeg;
	private int nLeg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_on_the_go);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		route = Route.getRouteFromString(sharedPreferences.getString("CurrentRoute", ""));

		nLeg = getIntent().getIntExtra("NLeg", 0);

		currentLeg = route.getLegs().get(nLeg);

		TextView line = (TextView) findViewById(R.id.line_otg);
		TextView previousS = (TextView) findViewById(R.id.previousStop_otg);
		TextView distance = (TextView) findViewById(R.id.distance_otg);
		TextView nextS = (TextView) findViewById(R.id.nextStop_otg);
		TextView nStops = (TextView) findViewById(R.id.stopsToCome_otg);
		TextView minRemaining = (TextView) findViewById(R.id.minutesToGo_otg);
		TextView minTotalRemaining = (TextView) findViewById(R.id.timeToFinalDestination_otg);

		line.setText(currentLeg.getLine().getName());
		//previousS
		RealTimeTracker.setDistanceTo(distance, currentLeg.getEndStop());	//da rivedere
		//nextS
		//nStops
		//minRemaining
		//minTotalRemaining
	}

	public void getOff(View view){
		if(nLeg == (route.getLegs().size() - 1)){
			Intent intent = new Intent(this, DestinationActivityB.class);
			startActivity(intent);
		}else{
			Intent intent = new Intent(this, OnTheGoActivity.class);
			intent.putExtra("NLeg", nLeg + 1);
			startActivity(intent);
		}
		finish();
	}
}
