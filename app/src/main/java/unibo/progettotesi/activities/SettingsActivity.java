package unibo.progettotesi.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Locale;

import unibo.progettotesi.R;
import unibo.progettotesi.utilities.Constants;
import unibo.progettotesi.utilities.LocationToolbox;
import unibo.progettotesi.utilities.VoiceSupport;

public class SettingsActivity extends AppCompatActivity {
	private boolean voiceSupport;
	private boolean locationPermission;
	private SharedPreferences sharedPreferences;
	private Switch switchV;
	private Switch switchL;
	private TextToSpeech tts;
	private double latitude = 0.0;
	private double longitude = 0.0;
	private LocationManager lm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		setTitle("Impostazioni");

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		voiceSupport = sharedPreferences.getBoolean("VoiceSupport", true);
		locationPermission = sharedPreferences.getBoolean("locationPermission", false);

		switchV = (Switch) findViewById(R.id.voiceSwitch);
		switchV.setChecked(voiceSupport);
		switchV.setTextOn("ON ");
		switchV.setTextOff("OFF");

		switchL = (Switch) findViewById(R.id.locationSwitch);
		switchL.setChecked(locationPermission);
		switchL.setTextOn("ON ");
		switchL.setTextOff("OFF");

		if(voiceSupport)
			tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
				@Override
				public void onInit(int status) {
					if(status != TextToSpeech.ERROR) {
						tts.setLanguage(Locale.getDefault());
					}
				}
			});

		final Context context = this;
		switchV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putBoolean("VoiceSupport", isChecked);
				editor.commit();

				if(!VoiceSupport.isTalkBackEnabled(context))
					Toast.makeText(context, "Supporto vocale " + (isChecked ? "attivato" : "disattivato"), Toast.LENGTH_SHORT).show();

				if(voiceSupport)
					tts.speak("Supporto vocale " + (isChecked ? "attivato" : "disattivato"), TextToSpeech.QUEUE_FLUSH, null);
			}
		});

		findViewById(R.id.voiceSupportRL).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switchV.toggle();
			}
		});


		switchL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putBoolean("locationPermission", isChecked);
				editor.commit();

				if (isChecked){
						/*LocationToolbox locationToolbox = new LocationToolbox((Activity) context);
						latitude = locationToolbox.getLatitude();
						longitude = locationToolbox.getLongitude();

						ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
								Constants.PERMISSION_LOCATION_REQUEST); */

						lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
						if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission((Activity) context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
							if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
									Manifest.permission.ACCESS_FINE_LOCATION)) {
								ActivityCompat.requestPermissions((Activity) context,
										new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
										Constants.PERMISSION_LOCATION_REQUEST);
							} else {
								ActivityCompat.requestPermissions((Activity) context,
										new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
										Constants.PERMISSION_LOCATION_REQUEST);
							}
						}else{
							try {
								Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
								if (location == null)
									location = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
					/*if (location == null)
					location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);*/
								if (location == null) {
									Toast.makeText(SettingsActivity.this, "Impossibile ottenere ultima posizione nota", Toast.LENGTH_SHORT).show();
									return;
								}
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}catch(Exception e){
								e.printStackTrace();
							}
						}

					} else {

					LocationToolbox locationToolbox = new LocationToolbox((Activity) context);
					locationToolbox.stopUsingGPS();
				}

				}


		});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case Constants.PERMISSION_LOCATION_REQUEST: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
					SharedPreferences.Editor editor = sp.edit();
					editor.putBoolean("locationPermission", true);
					editor.commit();
					try {
						if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
							return;
						}
						Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location == null)
							location = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
						/*if(location == null)
							location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);*/
						if(location == null){
							Toast.makeText(SettingsActivity.this, "Impossibile ottenere ultima posizione nota", Toast.LENGTH_SHORT).show();
							return;
						}
						latitude = location.getLatitude();
						longitude = location.getLongitude();
					}catch(Exception e){
						e.printStackTrace();
					}

				} else {
					Toast.makeText(this, "Permesso accesso posizione negato. La posizione è necessaria per diverse funzionalità dell'app", Toast.LENGTH_SHORT).show();
				}
				return;
			}
		}
	}

	@Override
	protected void onDestroy() {
		if(tts !=null){
			while(tts.isSpeaking()){}
			tts.stop();
			tts.shutdown();
		}

		super.onDestroy();
	}
}
