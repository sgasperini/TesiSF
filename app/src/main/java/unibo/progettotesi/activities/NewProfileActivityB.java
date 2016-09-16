package unibo.progettotesi.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_profile_activity_b);

		start = getIntent().getBooleanExtra("Start", false);
		gpsStart = getIntent().getBooleanExtra("GPS", false);
		//se è stato usato per la partenza il gps levalo
		if(gpsStart)
			findViewById(R.id.gps).setVisibility(View.GONE);

		if(start)
			this.setTitle("Partenza");
		else
			this.setTitle("Destinazione");

		tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if(status != TextToSpeech.ERROR) {
					tts.setLanguage(Locale.getDefault());
				}
			}
		});
	}

	public void gpsClick(View v){
		// verificare sia attivo (accenderlo), prendere la posizione e mostrarla come indirizzo chiedendo se si vuol salvare come preferito, comunque sia chiedere la distanza a piedi, poi chiamare la nuova activity
		Toast.makeText(NewProfileActivityB.this, "GPS", Toast.LENGTH_SHORT).show();
		tts.speak("GPS", TextToSpeech.QUEUE_FLUSH, null);
		
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

		Toast.makeText(NewProfileActivityB.this, "Preferiti", Toast.LENGTH_SHORT).show();
		tts.speak("Preferiti", TextToSpeech.QUEUE_FLUSH, null);
		
		startActivity(intent);
		finish();
	}

	public void addressClick(View v){
		Intent intent = new Intent(this, InputFormB.class);
		intent.putExtra("Start", start);
		intent.putExtra("Address", true);

		tts.speak("Indirizzo", TextToSpeech.QUEUE_FLUSH, null);
		Toast.makeText(NewProfileActivityB.this, "Indirizzo", Toast.LENGTH_SHORT).show();
		
		startActivity(intent);
		finish();
	}

	private void confirmAddress(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setMessage("Trovato:\n" + address + "\ncorretto?")
				.setCancelable(false)
				.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						saveLocation();
						askFavorite();
						dialog.cancel();
						locationToolbox.stopUsingGPS();
					}
				})

				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(getApplicationContext(), NewProfileActivityB.class);
						intent.putExtra("Start", start);
						startActivity(intent);
						dialog.cancel();
						locationToolbox.stopUsingGPS();
						finish();
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	private void askFavorite(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
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
			startActivity(intent);
			finish();
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
		startActivity(intent);
		finish();
	}

	private void saveLocation() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();

		if(start)
			editor.putString("StartTempPlace", place.savingString());
		else
			editor.putString("EndTempPlace", place.savingString());

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
}
