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
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import unibo.progettotesi.R;
import unibo.progettotesi.model.Leg;
import unibo.progettotesi.model.Route;
import unibo.progettotesi.model.Stop;
import unibo.progettotesi.utilities.LocationToolbox;
import unibo.progettotesi.utilities.RealTimeTracker;
import unibo.progettotesi.utilities.Time;

public class OnTheGoActivity extends Activity {
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

		nLeg = getIntent().getIntExtra("NLeg", 0);

		currentLeg = route.getLegs().get(nLeg);

		line = (TextView) findViewById(R.id.line_otg);
		previousS = (TextView) findViewById(R.id.previousStop_otg);
		distance = (TextView) findViewById(R.id.distance_otg);
		nextS = (TextView) findViewById(R.id.nextStop_otg);
		nStops = (TextView) findViewById(R.id.stopsToCome_otg);
		minRemaining = (TextView) findViewById(R.id.minutesToGo_otg);
		minTotalRemaining = (TextView) findViewById(R.id.timeToFinalDestination_otg);

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
		realTimeTracker.getStopsFromWeb(this, currentLeg);

	//	Log.wtf("TEST STOPS OTG", currentLeg.getInterStops().get(1).getName());
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
		locationToolbox.stopUsingGPS();
		finish();
	}


	public void setNewLeg(Leg currentLeg) {
		this.currentLeg = currentLeg;
		stopsToGo = currentLeg.getInterStops();
		previousStop = stopsToGo.get(0);
		stopsToGo.remove(0);
		updateViews(null);
	}

	private void updateViews(Location location) {
		previousS.setText(previousStop.getName());
		nextS.setText(stopsToGo.get(0).getName());
		nStops.setText("Fermate a scendere: " + stopsToGo.size());
		minRemaining.setText("Minuti a scendere: " + Time.getDifference(stopsToGo.get(0).getDepartureTime(), stopsToGo.get(stopsToGo.size() - 1).getDepartureTime()));
		if(location == null)
			distance.setText("Metri: " + (int) LocationToolbox.distance(previousStop.getLocation().getLatitude(), stopsToGo.get(0).getLocation().getLatitude(), previousStop.getLocation().getLongitude(), stopsToGo.get(0).getLocation().getLongitude(), 0.0, 0.0));
		else
			distance.setText("Metri: " + (int) LocationToolbox.distance(location.getLatitude(), stopsToGo.get(0).getLocation().getLatitude(), location.getLongitude(), stopsToGo.get(0).getLocation().getLongitude(), 0.0, 0.0));
	}






	private final class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			updateViews(location);

			//
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



}
