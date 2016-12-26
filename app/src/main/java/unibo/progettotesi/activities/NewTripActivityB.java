package unibo.progettotesi.activities;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import unibo.progettotesi.R;
import unibo.progettotesi.adapters.ProfilesAdapter;
import unibo.progettotesi.model.Profile;
import unibo.progettotesi.utilities.Constants;
import unibo.progettotesi.utilities.LocationToolbox;
import unibo.progettotesi.utilities.Time;
import unibo.progettotesi.utilities.VoiceSupport;

public class NewTripActivityB extends AppCompatActivity {
	private boolean start;
	private ProfilesAdapter profilesAdapter;
	private Button timeOption;
	private Button timePickerButton;
	private boolean departureTime = true;
	private CountDownTimer timer;
	private double latitude = 0.0;
	private double longitude = 0.0;
	private LocationManager lm;
	public static Handler finishHandler;
	private static TextToSpeech tts;
	private static boolean voiceSupport;

	@Override
	protected void onStart() {
		super.onStart();


		//tries to get the location, gets necessary permissions
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.ACCESS_FINE_LOCATION)) {

			} else {
				ActivityCompat.requestPermissions(this,
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
					Toast.makeText(NewTripActivityB.this, "Impossibile ottenere ultima posizione nota", Toast.LENGTH_SHORT).show();
					return;
				}
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			}catch(Exception e){
				e.printStackTrace();
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
							Toast.makeText(NewTripActivityB.this, "Impossibile ottenere ultima posizione nota", Toast.LENGTH_SHORT).show();
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_trip_activity_b);

		setTitle("Seleziona Profilo");

		List<Profile> profileList = getProfiles();

		profilesAdapter = new ProfilesAdapter(this, R.layout.profile_b_list, profileList);

		//at the creation of the activity, it sets the adapter for the profiles
		((ListView) findViewById(R.id.listView2)).setAdapter(profilesAdapter);

		timePickerButton = (Button) findViewById(R.id.buttonTimePicker);
		timePickerButton.setText(Time.now().toString());

		timeOption = (Button) findViewById(R.id.buttonPlanningTime);

		//the timer changes the time for the desired departure or arrival time
		timer = new CountDownTimer(1000000000, 1000) {

			public void onTick(long millisUntilFinished) {
				Calendar c = Calendar.getInstance();

				timePickerButton.setText(new Time(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)).toString());
			}
			public void onFinish() {

			}
		};
		timer.start();

		voiceSupport = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("VoiceSupport", true);

		if(voiceSupport)
			tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
				@Override
				public void onInit(int status) {
					if(status != TextToSpeech.ERROR) {
						tts.setLanguage(Locale.getDefault());
					}
				}
			});

		finishHandler = new Handler() {

			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				finish();
			}

		};
	}

	private List<Profile> getProfiles() {
		List<Profile> output = new ArrayList<Profile>();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		int numProfiles = preferences.getInt("NumProfiles", 0);
		for (int i = 1; i <= numProfiles; i++) {
			output.add(Profile.getProfileFromString(preferences.getString("ProfileN_" + i, "")));
			Profile profile = output.get(output.size() - 1);
			if(latitude != 0.0)
				output.get(output.size() - 1).setDistance((int) LocationToolbox.distance(latitude, profile.getStart().getLocation().getLatitude(), longitude, profile.getStart().getLocation().getLongitude(), 0.0, 0.0));
			else
				output.get(output.size() - 1).setDistance(new Integer(30000));
		}
		Collections.sort(output);
		return output;
	}

	public void startSingleTrip(View v){
		//when we want to sstart a single trip, we crate a fake profile using new profile, with gps as departure
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission((Activity) this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			if(voiceSupport)
				if(!VoiceSupport.isTalkBackEnabled(this)){
					tts.speak("Mancato permesso di accesso alla posizione GPS. Questo puà essere dato nelle impostazioni.", TextToSpeech.QUEUE_FLUSH, null);
				}
			Toast.makeText(this, "Mancato permesso di accesso alla posizione GPS.\n Questo puà essere dato nelle impostazioni.", Toast.LENGTH_SHORT).show();

		} else {
			Intent intent = new Intent(this, NewProfileActivityB.class);
			intent.putExtra("Start", false);
			intent.putExtra("GPS", true);
			intent.putExtra("singleTrip", true);

			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
			editor.putBoolean("departureTime", departureTime);
			editor.putString("time", timePickerButton.getText().toString());
			editor.commit();

			if (voiceSupport)
				if (!VoiceSupport.isTalkBackEnabled(this)) {
					tts.speak("Percorso singolo. GPS, attendere", TextToSpeech.QUEUE_FLUSH, null);
				}
			Toast.makeText(this, "Percorso singolo\nGPS, attendere", Toast.LENGTH_SHORT).show();

			startActivity(intent);
		}
	}

	public void openTimePicker(View v){
		if(voiceSupport)
			if(!VoiceSupport.isTalkBackEnabled(this)){
				tts.speak("Seleziona orario di " + (departureTime ? "partenza" : "arrivo"), TextToSpeech.QUEUE_FLUSH, null);
			}
		Toast.makeText(this, "Seleziona orario di " + (departureTime ? "partenza" : "arrivo"), Toast.LENGTH_SHORT).show();

		TimePickerDialog timePickerDialog = new TimePickerDialog(this,
				new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
										  int minute) {
						timer.cancel();
						timePickerButton.setText(new Time(hourOfDay,minute).toString());

						if(voiceSupport)
							if(!VoiceSupport.isTalkBackEnabled(view.getContext())){
								tts.speak("Selezionato: " + timePickerButton.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
							}
					}
				}, Time.now().getHour(), Time.now().getMinute(), false);
		timePickerDialog.show();
	}

	public void changeTimeOption(View v){
		departureTime = !departureTime;
		timeOption.setText((departureTime ? "Partenza alle" : "Arrivo alle"));

		if(voiceSupport)
			if(!VoiceSupport.isTalkBackEnabled(this)){
				tts.speak((departureTime ? "Partenza" : "Arrivo") + " alle: " + timePickerButton.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
			}
		Toast.makeText(this, (departureTime ? "Partenza" : "Arrivo") + " alle: " + timePickerButton.getText().toString(), Toast.LENGTH_SHORT).show();
	}

	//usual static method we call from the adapter when we select the item
	public static void selectProfile(NewTripActivityB newTripActivityB, Profile profile){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(newTripActivityB);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString("CurrentProfile", profile.savingString());
		editor.commit();

		if (ActivityCompat.checkSelfPermission(newTripActivityB, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission((Activity) newTripActivityB, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			if(voiceSupport)
				if(!VoiceSupport.isTalkBackEnabled(newTripActivityB)){
					tts.speak("Mancato permesso di accesso alla posizione GPS. Questo puà essere dato nelle impostazioni.", TextToSpeech.QUEUE_FLUSH, null);
				}
			Toast.makeText(newTripActivityB, "Mancato permesso di accesso alla posizione GPS.\n Questo puà essere dato nelle impostazioni.", Toast.LENGTH_SHORT).show();

		} else {

			if (newTripActivityB.voiceSupport)
				if (!VoiceSupport.isTalkBackEnabled(newTripActivityB)) {
					newTripActivityB.tts.speak("Seleziona percorso per: " + profile.getName(), TextToSpeech.QUEUE_FLUSH, null);
				}

			//launching the select route activity with the desired profile and departure/arrival time
			Intent intent = new Intent(newTripActivityB, SelectRouteActivityB.class);

			intent.putExtra("departureTime", newTripActivityB.departureTime);
			intent.putExtra("time", newTripActivityB.timePickerButton.getText().toString());

			newTripActivityB.startActivity(intent);
		}
	}

	@Override
	protected void onDestroy() {
		timer.cancel();
		super.onDestroy();

		if(tts !=null){
			while(tts.isSpeaking()){}
			tts.stop();
			tts.shutdown();
		}
	}
}
