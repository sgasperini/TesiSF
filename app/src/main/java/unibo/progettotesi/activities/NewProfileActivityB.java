package unibo.progettotesi.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import unibo.progettotesi.R;
import unibo.progettotesi.model.Location;
import unibo.progettotesi.model.Place;
import unibo.progettotesi.model.Profile;
import unibo.progettotesi.utilities.Constants;
import unibo.progettotesi.utilities.LocationToolbox;
import unibo.progettotesi.utilities.VoiceSupport;

public class NewProfileActivityB extends AppCompatActivity {
	private boolean start;
	private boolean gpsStart;
	private double latitude;
	private double longitude;
	private String address;
	private Location location;
	private Place place;
	private LocationToolbox locationToolbox;
	private TextToSpeech tts;
	private int editProfileN;
	private boolean departure;
	private Profile editingProfile;
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	private boolean voiceSupport;
	public static Handler finishHandlerStart;
	public static Handler finishHandlerEnd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_profile_activity_b);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		editor = sharedPreferences.edit();

		start = getIntent().getBooleanExtra("Start", false);
		gpsStart = getIntent().getBooleanExtra("GPS", false);
		editProfileN = getIntent().getIntExtra("editProfileN", -1);
		if(editProfileN != -1) {
			departure = getIntent().getBooleanExtra("departure", false);
			editingProfile = Profile.getProfileFromString(sharedPreferences.getString("ProfileN_" + editProfileN, ""));
		}

		//se è stato usato per la partenza il gps levalo
		if(gpsStart)
			findViewById(R.id.gps).setVisibility(View.GONE);

		if(start)
			this.setTitle("Partenza");
		else
			this.setTitle("Destinazione");
		if(editProfileN != -1){
			if(departure)
				this.setTitle("Nuova Partenza");
			else
				this.setTitle("Nuova Destinazione");
		}


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

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if(start)
			finishHandlerStart = new Handler() {

				public void handleMessage(Message msg) {
					super.handleMessage(msg);

					finish();
				}

			};
		else
			finishHandlerEnd = new Handler() {

				public void handleMessage(Message msg) {
					super.handleMessage(msg);

					finish();
				}

			};

		/*if (!VoiceSupport.isTalkBackEnabled(this))
			if((start && editProfileN == -1) || (editProfileN != -1 && departure)) {
				Log.wtf("VOICE", "Partenza...");
				tts.speak("Partenza, seleziona metodo immissione", TextToSpeech.QUEUE_FLUSH, null);
			}else{
				Log.wtf("VOICE", "Destinazione...");
				tts.speak("Destinazione, seleziona metodo immissione", TextToSpeech.QUEUE_FLUSH, null);
			}*/
	}

	public void gpsClick(View v){
		// verificare sia attivo (accenderlo), prendere la posizione e mostrarla come indirizzo chiedendo se si vuol salvare come preferito, comunque sia chiedere la distanza a piedi, poi chiamare la nuova activity
		if(!VoiceSupport.isTalkBackEnabled(this))
			Toast.makeText(NewProfileActivityB.this, "GPS, attendere", Toast.LENGTH_SHORT).show();
		if(voiceSupport)
			tts.speak("GPS, attendere", TextToSpeech.QUEUE_FLUSH, null);
		
		findViewById(R.id.progressBar_newProfile).setVisibility(View.VISIBLE);
		findViewById(R.id.gps).setVisibility(View.GONE);
		findViewById(R.id.preferiti).setVisibility(View.GONE);
		findViewById(R.id.indirizzo).setVisibility(View.GONE);
		findViewById(R.id.gps).setClickable(false);
		findViewById(R.id.preferiti).setClickable(false);
		findViewById(R.id.indirizzo).setClickable(false);

		locationToolbox = new LocationToolbox(this);
		latitude = locationToolbox.getLatitude();
		Log.wtf("LOCATION", "presa dentro gps click");
		longitude = locationToolbox.getLongitude();
		coordinatesToAddress();

		//RealTimeTracker.calculateDistances(null, locationToolbox.getLocation(), new Location(44.475409, 11.395950, ""), new Location(44.481295, 11.380806, ""));

		location = new Location(latitude, longitude, address);
		place = new Place(location);

		findViewById(R.id.progressBar_newProfile).setVisibility(View.GONE);
		findViewById(R.id.gps).setVisibility(View.VISIBLE);
		findViewById(R.id.preferiti).setVisibility(View.VISIBLE);
		findViewById(R.id.indirizzo).setVisibility(View.VISIBLE);
		findViewById(R.id.gps).setClickable(true);
		findViewById(R.id.preferiti).setClickable(true);
		findViewById(R.id.indirizzo).setClickable(true);

		confirmAddress();
	}

	private void coordinatesToAddress() {
		try{
			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

			address = addresses.get(0).getAddressLine(0);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void favoritesClick(View v){
		Intent intent = new Intent(this, FavoritesProfileB.class);
		intent.putExtra("Start", start);
		intent.putExtra("departure", departure);
		intent.putExtra("editProfileN", editProfileN);

		if(!VoiceSupport.isTalkBackEnabled(this))
			Toast.makeText(NewProfileActivityB.this, "Preferiti", Toast.LENGTH_SHORT).show();
		if(voiceSupport)
			tts.speak("Preferiti", TextToSpeech.QUEUE_FLUSH, null);
		
		startActivity(intent);
		//finish();
	}

	public void addressClick(View v){
		Intent intent;
		if(editProfileN == -1){
			intent = new Intent(this, InputFormB.class);
			intent.putExtra("Start", start);
			intent.putExtra("Address", true);
		}else{
			intent = new Intent(this, EditNameProfile.class);
			intent.putExtra("Address", true);
			intent.putExtra("editProfileN", editProfileN);
			intent.putExtra("departure", departure);
		}

		if(voiceSupport)
			tts.speak("Immettere Indirizzo", TextToSpeech.QUEUE_FLUSH, null);
		if(!VoiceSupport.isTalkBackEnabled(this))
			Toast.makeText(NewProfileActivityB.this, "Indirizzo", Toast.LENGTH_SHORT).show();
		
		startActivity(intent);
		//finish();
	}

	private void confirmAddress(){	//GPS
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		if(voiceSupport)
			if(!VoiceSupport.isTalkBackEnabled(this)){
				tts.speak("Trovato: " + address + " corretto?", TextToSpeech.QUEUE_FLUSH, null);
			}
		alertDialogBuilder
				.setTitle("Conferma")
				.setIcon(R.mipmap.ic_launcher)
				.setMessage("Trovato:\n" + address + "\ncorretto?")
				.setCancelable(false)
				.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(editProfileN == -1) {
							saveLocation();
							askFavorite();
							dialog.cancel();
							locationToolbox.stopUsingGPS();
						}else{
							if(departure)
								editingProfile.setStart(place);
							else
								editingProfile.setEnd(place);
							editor.putString("ProfileN_" + editProfileN, editingProfile.savingString());
							editor.commit();
							if(!VoiceSupport.isTalkBackEnabled(getApplicationContext()))
								Toast.makeText(NewProfileActivityB.this, "Profilo modificato", Toast.LENGTH_SHORT).show();
							if(voiceSupport)
								tts.speak("Profilo modificato", TextToSpeech.QUEUE_FLUSH, null);
							dialog.cancel();
							locationToolbox.stopUsingGPS();
							EditActivityB.finishHandler.sendEmptyMessage(0);
							EditDeleteActivityB.finishHandler.sendEmptyMessage(0);
							finish();
						}
					}
				})

				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(getApplicationContext(), NewProfileActivityB.class);
						intent.putExtra("Start", start);
						intent.putExtra("editProfileN", editProfileN);
						intent.putExtra("departure", departure);
						startActivity(intent);
						dialog.cancel();
						locationToolbox.stopUsingGPS();
						//finish();
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	private void askFavorite(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		if(voiceSupport)
			if(!VoiceSupport.isTalkBackEnabled(this)){
				tts.speak("Vuoi salvare il luogo tra i preferiti?", TextToSpeech.QUEUE_FLUSH, null);
			}
		alertDialogBuilder
				.setTitle("Preferiti")
				.setIcon(R.mipmap.ic_launcher)
				.setMessage("Vuoi salvare il luogo tra i preferiti?")
				.setCancelable(false)
				.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						startInputName(true);
					}
				})

				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						continueProfileCreation();
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	private void continueProfileCreation() {
		if(start){
			Intent intent = new Intent(getApplicationContext(), NewProfileActivityB.class);
			intent.putExtra("Start", !start);
			intent.putExtra("GPS", true);
			if(voiceSupport)
				tts.speak("Destinazione, selezionare metodo immissione", TextToSpeech.QUEUE_FLUSH, null);
			startActivity(intent);
			//finish();
		}else{
			startInputName(false);
		}
	}

	private void startInputName(boolean forFavorite) {
		Intent intent = new Intent(this, InputFormB.class);
		intent.putExtra("Start", start);
		intent.putExtra("Name", true);
		intent.putExtra("GPS", true);
		intent.putExtra("Favorite", forFavorite);
		if(voiceSupport)
			if(!VoiceSupport.isTalkBackEnabled(getApplicationContext())) {
				if (forFavorite)
					tts.speak("Immettere nome preferito", TextToSpeech.QUEUE_FLUSH, null);
				else
					tts.speak("Immettere nome profilo", TextToSpeech.QUEUE_FLUSH, null);
			}
		startActivity(intent);
		//finish();
	}

	private void saveLocation() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();

		if(editProfileN == -1) {
			if (start)
				editor.putString("StartTempPlace", place.savingString());
			else
				editor.putString("EndTempPlace", place.savingString());
		}else{
			if(departure)
				editingProfile.setStart(place);
			else
				editingProfile.setEnd(place);
		}

		editor.commit();
	}

	public static void saveProfile(Context context, String profileName) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();

		int numProfiles = preferences.getInt("NumProfiles", 0) + 1;
		Place placeStart = Place.getPlaceFromString(preferences.getString("StartTempPlace", ""));
		Place placeEnd = Place.getPlaceFromString(preferences.getString("EndTempPlace", ""));

		Profile profile = new Profile(placeStart, placeEnd, profileName);
		editor.putString("ProfileN_" + numProfiles, profile.savingString());
		editor.putInt("NumProfiles", numProfiles);

		editor.commit();

		ProfileManagingActivityB.finishHandler.sendEmptyMessage(0);
		try{
			NewProfileActivityB.finishHandlerEnd.sendEmptyMessage(0);
		}catch(Exception e){
			Log.wtf("BACK STACK", "finishHandlerEnd non funziona");
		}
		try{
			NewProfileActivityB.finishHandlerStart.sendEmptyMessage(0);
		}catch(Exception e){
			Log.wtf("BACK STACK", "finishHandlerStart non funziona");
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
						locationToolbox.getLocation();
						latitude = locationToolbox.getLatitude();
						Log.wtf("LOCATION", "presa dentro request permissions");
						longitude = locationToolbox.getLongitude();
					}catch(Exception e){
						e.printStackTrace();
					}

				} else {
					Toast.makeText(this.getApplicationContext(), "Permesso accesso posizione negato. La posizione è necessaria per diverse funzionalità dell'app", Toast.LENGTH_SHORT).show();
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
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
