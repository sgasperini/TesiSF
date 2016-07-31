package unibo.progettotesi.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import unibo.progettotesi.R;
import unibo.progettotesi.model.Leg;
import unibo.progettotesi.model.Route;
import unibo.progettotesi.model.Stop;
import unibo.progettotesi.utilities.Filler;
import unibo.progettotesi.utilities.HelloBus;
import unibo.progettotesi.utilities.LocationToolbox;
import unibo.progettotesi.utilities.RealTimeTracker;
import unibo.progettotesi.utilities.Time;

public class OnTheGoActivity extends Activity implements HelloBus{
	private Route route;
	private Leg currentLeg;
	private int nLeg;
	private Stop previousStop;
	private List<Stop> stopsToGo;
	private TextView line;
	private TextView previousS;
	private TextView distance;
	private TextView nextS;
	private TextView nStops;
	private TextView minRemaining;
	private TextView minTotalRemaining;
	LocationToolbox locationToolbox;
	private boolean isClose1;
	private int oldd0;
	private int oldd1;
	private boolean gettingClose1;
	private boolean roadDistance1;
	private int roadd0;
	private int roadd1;
	private int oldRoadd0;
	private int oldRoadd1;
	private boolean calculatedd1;
	private CountDownTimer timer;
	private String actualBus;

	@Override
	protected void onStart() {
		super.onStart();

		LocationListener locationListener = new MyLocationListener();
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_on_the_go);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		route = Route.getRouteFromString(sharedPreferences.getString("CurrentRoute", ""));

		actualBus = getIntent().getStringExtra("Bus");

		nLeg = getIntent().getIntExtra("NLeg", 0);

		currentLeg = route.getLegs().get(nLeg);

		line = (TextView) findViewById(R.id.line_otg);
		previousS = (TextView) findViewById(R.id.previousStop_otg);
		distance = (TextView) findViewById(R.id.distance_otg);
		nextS = (TextView) findViewById(R.id.nextStop_otg);
		nStops = (TextView) findViewById(R.id.stopsToCome_otg);
		minRemaining = (TextView) findViewById(R.id.minutesToGo_otg);
		minTotalRemaining = (TextView) findViewById(R.id.timeToFinalDestination_otg);

		Filler.fillRoute(findViewById(R.id.route_otg), route, this);
		findViewById(R.id.route_otg).setClickable(false);

		line.setText(currentLeg.getLine().getName());
		//previousS
		RealTimeTracker.setDistanceTo(distance, currentLeg.getEndStop());	//da rivedere
		//nextS
		//nStops
		//minRemaining
		//minTotalRemaining

		LocationToolbox locationToolbox = new LocationToolbox(this);
		locationToolbox.getLocation();

		RealTimeTracker realTimeTracker = new RealTimeTracker();
		realTimeTracker.getStopsFromWeb(this, currentLeg, true);

	//	Log.wtf("TEST STOPS OTG", currentLeg.getInterStops().get(1).getName());

		failure();

		timer = new CountDownTimer(500000000, 30000) {

			public void onTick(long millisUntilFinished) {
				if(actualBus != null)
					getETA();
				else
					failure();
			}

			public void onFinish() {
				//
			}
		};
		timer.start();
	}

	public void getOff(View view){
		if(nLeg == (route.getLegs().size() - 1)){
			Intent intent = new Intent(this, DestinationActivityB.class);
			startActivity(intent);
		}else{
			Intent intent = new Intent(this, BusWaitingActivity.class);
			intent.putExtra("NLeg", nLeg + 1);
			startActivity(intent);
		}
		if(locationToolbox != null)
			locationToolbox.stopUsingGPS();
		finish();
		timer.cancel();
	}

	private void getETA() {
		RealTimeTracker.getBusETA(this, currentLeg.getStartStop().getCode() + "", currentLeg.getLine().getName());
	}

	@Override
	public void setETA(Time time, String bus) {
		if(bus.equals(actualBus)){
			if(stopsToGo != null) {
				minRemaining.setText("Minuti a scendere:\n" + "stimati da satellite " + Time.getDifference(Time.now(), time) + "\nprevisti da orario " + Time.getDifference(Time.now(), stopsToGo.get(stopsToGo.size() - 1).getDepartureTime()));
				minTotalRemaining.setText("Minuti a destinazione:\nstimati da satellite non più di " + (Time.getDifference(Time.now(), route.getEndTime()) + Time.getDifference(stopsToGo.get(stopsToGo.size() - 1).getDepartureTime(), time)) + "\nprevisti " + Time.getDifference(Time.now(), route.getEndTime()));
			}
		}else
			failure();
	}

	public void failure(){
		if(stopsToGo != null)
			minRemaining.setText("Minuti a scendere:\nprevisti da orario " + Time.getDifference(Time.now(), stopsToGo.get(stopsToGo.size() - 1).getDepartureTime()));
		minTotalRemaining.setText("Minuti a destinazione:\nprevisti " + Time.getDifference(Time.now(), route.getEndTime()));
	}

	public void setNewLeg(Leg currentLeg) {
		this.currentLeg = currentLeg;
		stopsToGo = currentLeg.getInterStops();
		if(stopsToGo != null){
			previousStop = stopsToGo.get(0);
			stopsToGo.remove(0);
			updateViews(null);
		}
		getETA();
	}

	private void updateViews(Location location) {
		previousS.setText(previousStop.getName());
		nextS.setText(stopsToGo.get(0).getName());
		nStops.setText("Fermate a scendere: " + stopsToGo.size());
		if(location == null)
			distance.setText("Metri: " + (int) LocationToolbox.distance(previousStop.getLocation().getLatitude(), stopsToGo.get(0).getLocation().getLatitude(), previousStop.getLocation().getLongitude(), stopsToGo.get(0).getLocation().getLongitude(), 0.0, 0.0));
		else
			distance.setText("Metri: " + (int) LocationToolbox.distance(location.getLatitude(), stopsToGo.get(0).getLocation().getLatitude(), location.getLongitude(), stopsToGo.get(0).getLocation().getLongitude(), 0.0, 0.0));
	}

	private void locationUpdated(Location location){
		if(stopsToGo != null) {
			passingCondition(location);
			updateViews(location);
		}
	}

	private void passingCondition(Location location) {
		int d0 = ((int) LocationToolbox.distance(location.getLatitude(), stopsToGo.get(0).getLocation().getLatitude(), location.getLongitude(), stopsToGo.get(0).getLocation().getLongitude(), 0.0, 0.0));
		int d1 = ((int) LocationToolbox.distance(location.getLatitude(), stopsToGo.get(1).getLocation().getLatitude(), location.getLongitude(), stopsToGo.get(1).getLocation().getLongitude(), 0.0, 0.0));

		isClose(d0 < 20, (int) location.getAccuracy());
		gettingCloseNext(location, d0, d1);
	}

	private void gettingCloseNext(Location location, int d0, int d1) {
		if(d0 > oldd0 && d1 < oldd1){
			oldd0 = d0;
			oldd1 = d1;
			if(gettingClose1){
				calculatedd1 = false;
				roadDistance(location);
				return;
			}
			gettingClose1 = true;
			return;
		}
		gettingClose1 = false;
		roadDistance1 = false;
		oldd0 = d0;
		oldd1 = d1;
	}

	private void roadDistance(Location location) {
		//calcola le due distanze
		RealTimeTracker.calculateDistances(this, location, stopsToGo.get(0).getLocation(), stopsToGo.get(1).getLocation());
	}

	private void isClose(boolean d, int acc) {		//è a meno di 20m con precisione di max 20m
		if(acc < 20 && d){
			if(isClose1)
				stopPassed();
			isClose1 = true;
		}else{
			if(!d)
				isClose1 = false;
		}
	}

	private void stopPassed() {
		previousStop = stopsToGo.get(0);
		stopsToGo.remove(0);
		isClose1 = false;
		gettingClose1 = false;
		roadDistance1 = false;
		oldd0 = 0;
		oldd1 = 0;
	}




	private final class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			locationUpdated(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// called when the GPS provider is turned off (user turning off the GPS on the phone)
		}

		@Override
		public void onProviderEnabled(String provider) {
			// called when the GPS provider is turned on (user turning on the GPS on the phone)
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// called when the status of the GPS provider changes
		}
	}


	public int getRoadd0() {
		return roadd0;
	}

	public void setRoadd0(int roadd0) {
		this.roadd0 = roadd0;
		compare();
	}

	private void compare() {
		if(!calculatedd1)
			calculatedd1 = true;
		else{
			if(roadDistance1){
				if(roadd0 > oldRoadd0 && roadd1 < oldRoadd1) {
					stopPassed();
					updateViews(null);
				}
			}else {
				roadDistance1 = true;
				calculatedd1 = false;
				oldRoadd0 = roadd0;
				oldRoadd1 = roadd1;
			}
		}
	}

	public int getRoadd1() {
		return roadd1;
	}

	public void setRoadd1(int roadd1) {
		this.roadd1 = roadd1;
		compare();
	}

	@Override
	protected void onDestroy() {
		timer.cancel();
		super.onDestroy();
	}
}
