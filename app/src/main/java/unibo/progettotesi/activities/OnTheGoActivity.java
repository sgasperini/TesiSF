package unibo.progettotesi.activities;

import android.Manifest;
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
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import unibo.progettotesi.R;
import unibo.progettotesi.model.Leg;
import unibo.progettotesi.model.Route;
import unibo.progettotesi.model.Stop;
import unibo.progettotesi.utilities.Constants;
import unibo.progettotesi.utilities.Filler;
import unibo.progettotesi.utilities.HelloBus;
import unibo.progettotesi.utilities.LocationToolbox;
import unibo.progettotesi.utilities.RealTimeTracker;
import unibo.progettotesi.utilities.Time;
import unibo.progettotesi.utilities.VoiceSupport;
import unibo.progettotesi.utilities.Walking;

public class OnTheGoActivity extends AppCompatActivity implements HelloBus, Walking {
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
	private LocationListener locationListener = new MyLocationListener();;
	private LocationManager lm;
	private boolean updates = false;
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	private TextToSpeech tts;
	private boolean vibration = false;
	private boolean shownDialog = false;
	private boolean voiceSupport;

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
		} else {
			if (!updates) {
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
				updates = true;
			}
		}
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
						if(!updates) {
							lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
							updates = true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					Toast.makeText(this, "Permesso accesso posizione negato. La posizione è necessaria per diverse funzionalità dell'app", Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.activity_on_the_go);

		setTitle("In Viaggio");

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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

		findViewById(R.id.progressBar_otg).setVisibility(View.VISIBLE);
		previousS.setVisibility(View.GONE);
		nextS.setVisibility(View.GONE);
		distance.setVisibility(View.GONE);
		nStops.setVisibility(View.GONE);
		minRemaining.setVisibility(View.GONE);

		Filler.fillRoute(findViewById(R.id.route_otg), route, this);
		findViewById(R.id.route_otg).setClickable(false);

		line.setText("Linea: " + currentLeg.getLine().getName());
		//previousS
		RealTimeTracker.setDistanceTo(distance, currentLeg.getEndStop());    //da rivedere
		//nextS
		//nStops
		//minRemaining
		//minTotalRemaining

	/*	LocationToolbox locationToolbox = new LocationToolbox(this);
		locationToolbox.getLocation();*/

		String lastStopS = getIntent().getStringExtra("LastStop");
		Log.wtf("CrashDetector OTG stop", lastStopS);
		if(lastStopS != null && !lastStopS.equals("")){
			currentLeg.setStartStop(Stop.getStopFromStringEmergency(lastStopS));
			currentLeg.setStartTime(currentLeg.getStartStop().getDepartureTime());
		}

		RealTimeTracker realTimeTracker = new RealTimeTracker();
		realTimeTracker.getStopsFromWeb(this, currentLeg, true);

		//	Log.wtf("TEST STOPS OTG", currentLeg.getInterStops().get(1).getName());

		failure();

		timer = new CountDownTimer(500000000, 30000) {

			public void onTick(long millisUntilFinished) {
				if (actualBus != null)
					getETA();
				else
					failure();
			}

			public void onFinish() {
				//
			}
		};
		timer.start();

		editor = sharedPreferences.edit();

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
	}

	public void getOff(View view) {
		final OnTheGoActivity onTheGoActivity = this;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		if(voiceSupport)
			if(!VoiceSupport.isTalkBackEnabled(this)){
				tts.speak("Confermi di voler scendere dall'autobus?", TextToSpeech.QUEUE_FLUSH, null);
			}
		alertDialogBuilder
				.setTitle("Conferma")
				.setIcon(R.mipmap.ic_launcher)
				.setMessage("Confermi di voler scendere dall'autobus?")
				.setCancelable(false)
				.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(!VoiceSupport.isTalkBackEnabled(onTheGoActivity))
							Toast.makeText(OnTheGoActivity.this, "Discesa", Toast.LENGTH_SHORT).show();
						if(voiceSupport)
							tts.speak("Discesa", TextToSpeech.QUEUE_FLUSH, null);

						if (nLeg == (route.getLegs().size() - 1)) {
							Intent intent = new Intent(onTheGoActivity, DestinationActivityB.class);
							startActivity(intent);
						} else {
							Intent intent = new Intent(onTheGoActivity, BusWaitingActivity.class);
							intent.putExtra("NLeg", nLeg + 1);
							startActivity(intent);
						}
	/*	if(locationToolbox != null)
			locationToolbox.stopUsingGPS();*/

						editor.putString("LastStop", "");
						editor.commit();

						dialog.cancel();

						finish();
						timer.cancel();
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

	private void getETA() {
		RealTimeTracker.getBusETA(this, currentLeg.getStartStop().getCode() + "", currentLeg.getLine().getName());
	}

	@Override
	public void setETA(Time time, String bus) {
		if (bus.equals(actualBus)) {
			if (stopsToGo != null) {
				minRemaining.setText("Minuti a scendere:\n" + "da satellite " + Time.getDifference(Time.now(), time) + "\nda orario " + Time.getDifference(Time.now(), stopsToGo.get(stopsToGo.size() - 1).getDepartureTime()));
				minTotalRemaining.setText("Minuti a destinazione:\nda satellite non più di " + (Time.getDifference(Time.now(), route.getEndTime()) + Time.getDifference(stopsToGo.get(stopsToGo.size() - 1).getDepartureTime(), time)) + "\nda orario " + Time.getDifference(Time.now(), route.getEndTime()));
			}
		} else
			failure();
	}

	public void failure() {
		if (stopsToGo != null)
			minRemaining.setText("Minuti a scendere:\nda orario " + Time.getDifference(Time.now(), stopsToGo.get(stopsToGo.size() - 1).getDepartureTime()));
		minTotalRemaining.setText("Minuti a destinazione:\nda orario " + Time.getDifference(Time.now(), route.getEndTime()));
	}

	public void setNewLeg(Leg currentLeg) {
		this.currentLeg = currentLeg;
		stopsToGo = currentLeg.getInterStops();

		if (stopsToGo != null) {
			previousStop = stopsToGo.get(0);
			stopsToGo.remove(0);
			updateViews(null);
		}

		findViewById(R.id.progressBar_otg).setVisibility(View.GONE);
		previousS.setVisibility(View.VISIBLE);
		nextS.setVisibility(View.VISIBLE);
		distance.setVisibility(View.VISIBLE);
		nStops.setVisibility(View.VISIBLE);
		minRemaining.setVisibility(View.VISIBLE);

		getETA();
	}

	private void updateViews(Location location) {
		previousS.setText(previousStop.getName());
		nextS.setText(stopsToGo.get(0).getName());
		nStops.setText("Fermate a scendere: " + (stopsToGo.size() - 1));
		if (location == null)
			distance.setText("Metri: " + (int) LocationToolbox.distance(previousStop.getLocation().getLatitude(), stopsToGo.get(0).getLocation().getLatitude(), previousStop.getLocation().getLongitude(), stopsToGo.get(0).getLocation().getLongitude(), 0.0, 0.0));
		else
			distance.setText("Metri: " + (int) LocationToolbox.distance(location.getLatitude(), stopsToGo.get(0).getLocation().getLatitude(), location.getLongitude(), stopsToGo.get(0).getLocation().getLongitude(), 0.0, 0.0));
	}

	private void locationUpdated(Location location) {
		if (stopsToGo != null && stopsToGo.size() > 1) {
			passingCondition(location);
			updateViews(location);
		} else{
			if (stopsToGo != null && stopsToGo.size() == 1) {
				if(!shownDialog) {
					shownDialog = true;
					vibration = true;

					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
					if(voiceSupport)
						if(!VoiceSupport.isTalkBackEnabled(this)){
							tts.speak("Scendere alla prossima fermata, conferma per fermare avvisi", TextToSpeech.QUEUE_FLUSH, null);
						}
					alertDialogBuilder
							.setTitle("Discesa")
							.setIcon(R.mipmap.ic_launcher)
							.setMessage("Scendere alla prossima fermata, conferma per fermare avvisi")
							.setCancelable(false)
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									vibration = false;
									dialog.cancel();
								}
							});

					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}

				updateViews(location);
				if(vibration) {
					((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(200);
					Toast.makeText(OnTheGoActivity.this, "SCENDI ALLA PROSSIMA!", Toast.LENGTH_SHORT).show();
					Log.wtf("RINGRAZIA IL LOG", "SCENDI ALLA PROSSIMA!");
				}
			}
		}
	}

	private void passingCondition(Location location) {
		int d0 = ((int) LocationToolbox.distance(location.getLatitude(), stopsToGo.get(0).getLocation().getLatitude(), location.getLongitude(), stopsToGo.get(0).getLocation().getLongitude(), 0.0, 0.0));
		int d1;
		if (stopsToGo.size() != 1)
			d1 = ((int) LocationToolbox.distance(location.getLatitude(), stopsToGo.get(1).getLocation().getLatitude(), location.getLongitude(), stopsToGo.get(1).getLocation().getLongitude(), 0.0, 0.0));
		else
			d1 = -100;

		Log.wtf("FERMATE", "\td0 = " + d0 + " d1 = " + d1 + " acc = " + (int) location.getAccuracy());

		if (d0 < 20) {
			isClose((int) location.getAccuracy());
		}/*else{
			isClose1 = false;
		}*/
		if (d1 > 0)
			gettingCloseNext(location, d0, d1);
	}

	private void gettingCloseNext(Location location, int d0, int d1) {
		if (d0 > oldd0 && d1 < oldd1) {
			Log.wtf("FERMATE", "\t\td0 = " + d0 + " oldd0 = " + oldd0 + " d1 = " + d1 + " oldd1 = " + oldd1);
			oldd0 = d0;
			oldd1 = d1;
			if (gettingClose1) {
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

	private void isClose(int acc) {        //è a meno di 20m con precisione di max 20m
		if (acc < 20) {
			Log.wtf("FERMATE", "\t\t\tisClose, acc = " + acc);
			//if(isClose1)
			stopPassed();
			//isClose1 = true;
		}
	}

	private void stopPassed() {
		if(voiceSupport)
			tts.speak("Fermata " + stopsToGo.get(0).getName() + " superata, Prossima fermata: " + stopsToGo.get(1).getName() + ". Fermate a scendere: " + (stopsToGo.size() - 2), TextToSpeech.QUEUE_FLUSH, null);
		Log.wtf("FERMATE", previousStop.getName() + " -> " + stopsToGo.get(0).getName());
		previousStop = stopsToGo.get(0);
		editor.putString("LastStop", previousStop.savingStringEmergency());
		editor.commit();
		stopsToGo.remove(0);
		// SI METTE UN CONTROLLO SE è VUOTO DOVEVI SCENDERE
		isClose1 = false;
		gettingClose1 = false;
		roadDistance1 = false;
		oldd0 = 0;
		oldd1 = 0;
	}

	@Override
	public void setDistance(double length) {
	}

	@Override
	public void failureDistance() {
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
		Log.wtf("FERMATE", "\t\t\troadd0 = " + roadd0);
		oldRoadd0 = this.roadd0;
		this.roadd0 = roadd0;
		compare();
	}

	private void compare() {
		if (!calculatedd1)
			calculatedd1 = true;
		else {
			if (roadDistance1) {
				Log.wtf("FERMATE", "\t\t\troadd0 = " + roadd0 + " oldRoadd0 = " + oldRoadd0 + " roadd1 = " + roadd1 + " oldRoadd1 = " + oldRoadd1);
				if (roadd0 > oldRoadd0 && roadd1 < oldRoadd1) {
					stopPassed();
					updateViews(null);
				}
			} else {
				roadDistance1 = true;
				calculatedd1 = false;
				/*oldRoadd0 = roadd0;
				oldRoadd1 = roadd1;*/
			}
		}
	}

	public int getRoadd1() {
		return roadd1;
	}

	public void setRoadd1(int roadd1) {
		Log.wtf("FERMATE", "\t\t\troadd1 = " + roadd1);
		oldRoadd1 = this.roadd1;
		this.roadd1 = roadd1;
		compare();
	}

	@Override
	protected void onDestroy() {

		Log.wtf("OnTheGoActivity", "ON DESTROY");
		timer.cancel();
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			//
		}else if(lm != null){
			Log.wtf("LOCATION UPDATES", "REMOVING OTG ON DESTROY");
			lm.removeUpdates(locationListener);
			lm = null;
		}
		if(locationListener != null)
			locationListener = null;
		editor.putString("LastStop", "");
		editor.putString("CurrentRoute", "");
		editor.commit();

		if(tts !=null){
			while(tts.isSpeaking()){}
			tts.stop();
			tts.shutdown();
		}

		super.onDestroy();
	}

	 public void onBackPressed(){
		 final OnTheGoActivity onTheGoActivity = this;
		 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		 if(voiceSupport)
			 if(!VoiceSupport.isTalkBackEnabled(this)){
			 	tts.speak("Confermi di tornare indietro e perdere il percorso corrente?", TextToSpeech.QUEUE_FLUSH, null);
			 }
		 alertDialogBuilder
				 .setTitle("Conferma")
				 .setIcon(R.mipmap.ic_launcher)
				 .setMessage("Confermi di tornare indietro e perdere il percorso corrente?")
				 .setCancelable(false)
				 .setPositiveButton("Sì", new DialogInterface.OnClickListener() {
					 @Override
					 public void onClick(DialogInterface dialog, int which) {
						 timer.cancel();
						 if (ActivityCompat.checkSelfPermission(onTheGoActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(onTheGoActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
							 //
						 }else if(lm != null){
							 Log.wtf("LOCATION UPDATES", "REMOVING OTG BACK");
							 lm.removeUpdates(locationListener);
							 lm = null;
						 }
						 if(locationListener != null)
							 locationListener = null;
						 editor.putString("LastStop", "");
						 editor.putString("CurrentRoute", "");
						 editor.commit();
						 OnTheGoActivity.super.onBackPressed();
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
}
