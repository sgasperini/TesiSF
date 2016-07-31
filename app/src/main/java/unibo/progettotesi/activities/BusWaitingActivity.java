package unibo.progettotesi.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import unibo.progettotesi.R;
import unibo.progettotesi.model.Route;
import unibo.progettotesi.utilities.Filler;
import unibo.progettotesi.utilities.RealTimeTracker;

public class BusWaitingActivity extends Activity {
	private Route route;
	private int nLeg;
	private CountDownTimer timer;

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

		getETA();

		timer = new CountDownTimer(500000000, 60000) {

			public void onTick(long millisUntilFinished) {
				getETA();
			}

			public void onFinish() {
				//
			}
		};
		timer.start();
	}

	private void getETA() {
		RealTimeTracker.getBusETA((TextView) findViewById(R.id.firstLeg).findViewById(R.id.busStartRealTime_leg), route.getLegs().get(nLeg).getStartStop().getCode() + "", route.getLegs().get(nLeg).getLine().getName());
	}


	public void getOn(View view) {
		//start next
		Intent intent = new Intent(this, OnTheGoActivity.class);
		intent.putExtra("NLeg", nLeg);
		startActivity(intent);

		timer.cancel();
		finish();
	}

	@Override
	protected void onDestroy() {
		timer.cancel();
		super.onDestroy();
	}
}
