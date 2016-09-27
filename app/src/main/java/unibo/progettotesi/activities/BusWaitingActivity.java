package unibo.progettotesi.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import unibo.progettotesi.R;
import unibo.progettotesi.model.Route;
import unibo.progettotesi.utilities.Constants;
import unibo.progettotesi.utilities.Filler;
import unibo.progettotesi.utilities.HelloBus;
import unibo.progettotesi.utilities.RealTimeTracker;
import unibo.progettotesi.utilities.Time;
import unibo.progettotesi.utilities.VoiceSupport;
import unibo.progettotesi.utilities.Walking;

public class BusWaitingActivity extends AppCompatActivity implements HelloBus, Walking {
	private Route route;
	public static int nLeg;
	private CountDownTimer timer;
	private BusWaitingActivity busWaitingActivity;
	public static String bus = null;
	private LocationListener locationListener = new WalkingLocationListener();
	private LocationManager lm;
	private boolean failedDistance = false;
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	private boolean otg;
	private boolean updates = false;
	private TextToSpeech tts;
	private boolean voiceSupport;
	public static Handler notificationHandler;

	@Override
	protected void onStart() {
		super.onStart();

		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.ACCESS_FINE_LOCATION)) {

				// Show an expanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.

			} else {
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						Constants.PERMISSION_LOCATION_REQUEST);
			}
		}else{
			failureDistance();
			if (!updates) {
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, locationListener);
				updates = true;
			}
		}

		busWaitingActivity = this;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case Constants.PERMISSION_LOCATION_REQUEST: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					try {
						if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
							return;
						}
						if (!updates) {
							lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, locationListener);
							updates = true;
						}
					}catch(Exception e){
						e.printStackTrace();
					}

				} else {
					Toast.makeText(BusWaitingActivity.this, "Permesso accesso posizione negato. La posizione è necessaria per diverse funzionalità dell'app", Toast.LENGTH_SHORT).show();
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bus_waiting);

		setTitle("Alla Fermata");

		otg = false;

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		editor = sharedPreferences.edit();

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

		findViewById(R.id.secondLeg).findViewById(R.id.distance_leg).setVisibility(View.GONE);
		findViewById(R.id.thirdLeg).findViewById(R.id.distance_leg).setVisibility(View.GONE);

		voiceSupport = sharedPreferences.getBoolean("VoiceSupport", true);

		if(voiceSupport)
			tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
				@Override
				public void onInit(int status) {
					if(status != TextToSpeech.ERROR) {
						tts.setLanguage(Locale.getDefault());
					}
				}
			});

		MainActivity.startActivityHandlerBusWaiting.sendEmptyMessage(0);

		notificationHandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				getActuallyOn();
			}
		};
	}

	private void getETA() {
		RealTimeTracker.getBusETA(this, route.getLegs().get(0).getStartStop().getCode() + "", route.getLegs().get(0).getLine().getName());
	}

	@Override
	public void setETA(Time time, String bus) {
		int difference = Time.getDifference(route.getLegs().get(0).getStartTime(), time);
		if(difference > -5 && difference < 45){
			((TextView) findViewById(R.id.firstLeg).findViewById(R.id.busStartRealTime_leg)).setText(
					"Bus previsto alle: " + route.getLegs().get(0).getStartTime() + "\nstimato da satellite in " + (difference == 0 ? "orario" :
							(difference > 0 ? "ritardo" : "anticipo") + " di " + Math.abs(difference) + " minuti (" + time + ")"));
			this.bus = bus;
			if((difference = Math.abs(Time.getDifference(time, Time.now()))) == 0) {
				if (voiceSupport)
					if (!VoiceSupport.isTalkBackEnabled(this)) {
						tts.speak("Autobus in arrivo a breve, prepararsi", TextToSpeech.QUEUE_FLUSH, null);
					}
			}else if(difference <= 5){
				if (voiceSupport)
					if (!VoiceSupport.isTalkBackEnabled(this)) {
						tts.speak("Autobus in arrivo tra " + difference + (difference != 1 ? "minuti" : "minuto") + ". Tenersi pronti", TextToSpeech.QUEUE_FLUSH, null);
					}
			}
		}else
			failure();
	}

	public void failure(){
		((TextView) findViewById(R.id.firstLeg).findViewById(R.id.busStartRealTime_leg)).setText(
				"Bus previsto alle: " + route.getLegs().get(0).getStartTime());
		int difference;
		if((difference = Time.getDifference(Time.now(), route.getLegs().get(0).getStartTime())) == 0) {
			if (voiceSupport)
				if (!VoiceSupport.isTalkBackEnabled(this)) {
					tts.speak("Autobus in arrivo a breve da orario, prepararsi", TextToSpeech.QUEUE_FLUSH, null);
				}
		}else if(difference <= 5){
			if (voiceSupport)
				if (!VoiceSupport.isTalkBackEnabled(this)) {
					tts.speak("Autobus in arrivo da orario tra " + difference + (difference != 1 ? "minuti" : "minuto") + ". Tenersi pronti", TextToSpeech.QUEUE_FLUSH, null);
				}
		}
	}

	public void failureDistance(){
		failedDistance = true;
		((TextView) findViewById(R.id.firstLeg).findViewById(R.id.distance_leg)).setVisibility(View.GONE);
	}

	public void getOn(View view) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		if(voiceSupport)
			if(!VoiceSupport.isTalkBackEnabled(this)){
				tts.speak("Confermi di salire?", TextToSpeech.QUEUE_FLUSH, null);
			}
		alertDialogBuilder
				.setTitle("Conferma")
				.setIcon(R.mipmap.ic_launcher)
				.setMessage("Confermi di salire?")
				.setCancelable(false)
				.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						getActuallyOn();
						BusWaitingActivity.super.onBackPressed();
					}
				})

				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	public void getActuallyOn(){
		Intent intent = new Intent(this, OnTheGoActivity.class);
		intent.putExtra("NLeg", nLeg);
		if(bus != null)
			intent.putExtra("Bus", bus);
		startActivity(intent);

		if(!VoiceSupport.isTalkBackEnabled(this))
			Toast.makeText(BusWaitingActivity.this, "Sali", Toast.LENGTH_SHORT).show();
		if(voiceSupport)
			tts.speak("Sali", TextToSpeech.QUEUE_FLUSH, null);

		otg = true;
		timer.cancel();

		try {
			MainActivity.endActivityHandlerBusWaiting.sendEmptyMessage(0);
			NewTripActivityB.finishHandler.sendEmptyMessage(0);
			SelectRouteActivityB.finishHandler.sendEmptyMessage(0);
		}catch(Exception e){
			Log.wtf("BusWaitingActivity", "PROBLEMA CON GLI HANDLER");
		}

		finish();
	}

	@Override
	protected void onDestroy() {

		Log.wtf("BusWaitingActivity", "ON DESTROY");
		timer.cancel();
		if(!otg){
			editor.putString("CurrentRoute", "");
			editor.commit();
		}
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			//
		}else if(lm != null){
			Log.wtf("LOCATION UPDATES", "REMOVING BUSWAITING ON DESTROY");
			lm.removeUpdates(locationListener);
			lm = null;
		}
		if(locationListener != null)
			locationListener = null;

		if(tts !=null){
			while(tts.isSpeaking()){}
			tts.stop();
			tts.shutdown();
		}

		MainActivity.endActivityHandlerBusWaiting.sendEmptyMessage(0);

		super.onDestroy();
	}

	public void onBackPressed(){
		if (ActivityCompat.checkSelfPermission(busWaitingActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(busWaitingActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			//
		}else if(lm != null){
			Log.wtf("LOCATION UPDATES", "REMOVING BUSWAITING BACK");
			lm.removeUpdates(locationListener);
			lm = null;
		}
		if(locationListener != null)
			locationListener = null;
		editor.putString("CurrentRoute", "");
		editor.commit();
		BusWaitingActivity.super.onBackPressed();
	}

	private void getWalkingDistance(Location location){

	}

	public void setDistance(double length) {
		if(failedDistance) {
			failedDistance = false;
			((TextView) findViewById(R.id.firstLeg).findViewById(R.id.distance_leg)).setVisibility(View.VISIBLE);
		}
		((TextView) findViewById(R.id.firstLeg).findViewById(R.id.distance_leg)).setText((int) length + " metri");
	}



	private final class WalkingLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			RealTimeTracker.calculateWalkingDistance(busWaitingActivity, location, route.getLegs().get(0).getStartStop().getLocation());
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
