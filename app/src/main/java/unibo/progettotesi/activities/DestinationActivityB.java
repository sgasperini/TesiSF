package unibo.progettotesi.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
import unibo.progettotesi.model.Profile;
import unibo.progettotesi.model.Route;
import unibo.progettotesi.utilities.Constants;
import unibo.progettotesi.utilities.RealTimeTracker;
import unibo.progettotesi.utilities.VoiceSupport;
import unibo.progettotesi.utilities.Walking;

public class DestinationActivityB extends AppCompatActivity implements Walking{
	private LocationListener locationListener = new WalkingLocationListener();
	private LocationManager lm;
	private DestinationActivityB destinationActivityB;
	private Route route;
	private TextView meters;
	private boolean failedDistance = false;
	private TextView infoWalking;
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	private boolean updates = false;
	private TextToSpeech tts;
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
		}else{
			failureDistance();

			if (!updates) {
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, locationListener);
				updates = true;
			}
		}

		destinationActivityB = this;
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
		setContentView(R.layout.destination_activity_b);

		setTitle("Cammino a Destinazione");

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		editor = sharedPreferences.edit();

		Profile profile = Profile.getProfileFromString(sharedPreferences.getString("CurrentProfile", ""));
		route = Route.getRouteFromString(sharedPreferences.getString("CurrentRoute", ""));

		TextView stop = (TextView) findViewById(R.id.endStop_dest);
		meters = (TextView) findViewById(R.id.walking_dest);
		infoWalking = (TextView) findViewById(R.id.walking_dest);
		TextView destination = (TextView) findViewById(R.id.destination_dest);

		stop.setText(route.getEndStop().getName());
		//meters
		destination.setText(profile.getEnd().getLocation().getAddress());

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

	@Override
	public void setDistance(double length) {
		if(failedDistance) {
			failedDistance = false;
			meters.setVisibility(View.VISIBLE);
			infoWalking.setText("per arrivare a");
		}
		meters.setText((int) length + " metri");
	}

	public void failureDistance(){
		failedDistance = true;
		meters.setVisibility(View.GONE);
		infoWalking.setText("verso");
	}

	private final class WalkingLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			RealTimeTracker.calculateWalkingDistance(destinationActivityB, location, route.getEndPlace().getLocation());
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

	@Override
	protected void onDestroy() {

		Log.wtf("DestinationActivity", "ON DESTROY");
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			//
		}else if(lm != null){
			Log.wtf("LOCATION UPDATES", "REMOVING DEST ON DESTROY");
			lm.removeUpdates(locationListener);
			lm = null;
		}
		if(locationListener != null)
			locationListener = null;
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
		final DestinationActivityB destinationActivityB = this;
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
						if (ActivityCompat.checkSelfPermission(destinationActivityB, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(destinationActivityB, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
							//
						}else if(lm != null){
							Log.wtf("LOCATION UPDATES", "REMOVING DEST BACK");
							lm.removeUpdates(locationListener);
							lm = null;
						}
						if(locationListener != null)
							locationListener = null;
						editor.putString("CurrentRoute", "");
						editor.commit();
						dialog.cancel();
						DestinationActivityB.super.onBackPressed();
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
