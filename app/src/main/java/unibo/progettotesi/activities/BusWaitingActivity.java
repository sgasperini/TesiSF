package unibo.progettotesi.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;

import unibo.progettotesi.R;
import unibo.progettotesi.adapters.RoutesAdapter;
import unibo.progettotesi.model.Profile;
import unibo.progettotesi.model.Route;
import unibo.progettotesi.utilities.Filler;
import unibo.progettotesi.utilities.RouteFinder;
import unibo.progettotesi.utilities.Time;

public class BusWaitingActivity extends Activity {
	private Route route;
	private int nLeg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bus_waiting);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		route = Route.getRouteFromString(sharedPreferences.getString("CurrentRoute", ""));

		Filler.fillRoute(findViewById(R.id.routeLayout), route, this);
		findViewById(R.id.routeLayout).setClickable(false);

		nLeg = getIntent().getIntExtra("NLeg", 0);
		if(nLeg > 0){
			route.getLegs().remove(0);
			if(nLeg > 1){
				route.getLegs().remove(0);
			}
		}

		Filler.fillLeg(findViewById(R.id.firstLeg), route.getLegs().get(0));
		int nLegs = route.getLegs().size();
		if(nLegs < 3){
			findViewById(R.id.thirdLeg).setVisibility(View.GONE);
			if(nLegs < 2)
				findViewById(R.id.secondLeg).setVisibility(View.GONE);
			else
				Filler.fillLeg(findViewById(R.id.secondLeg), route.getLegs().get(1));
		}else{
			Filler.fillLeg(findViewById(R.id.secondLeg), route.getLegs().get(1));
			Filler.fillLeg(findViewById(R.id.thirdLeg), route.getLegs().get(2));
		}


	}

	public void getOn(View view) {
		//start next
		Intent intent = new Intent(this, OnTheGoActivity.class);
		intent.putExtra("NLeg", nLeg);
		startActivity(intent);

		finish();
	}
}
