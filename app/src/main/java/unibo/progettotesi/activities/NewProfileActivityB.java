package unibo.progettotesi.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.List;
import java.util.Locale;

import unibo.progettotesi.R;
import unibo.progettotesi.model.Location;
import unibo.progettotesi.model.Place;
import unibo.progettotesi.model.Profile;
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
	}

	public void gpsClick(View v){
		// verificare sia attivo (accenderlo), prendere la posizione e mostrarla come indirizzo chiedendo se si vuol salvare come preferito, comunque sia chiedere la distanza a piedi, poi chiamare la nuova activity
		locationToolbox = new LocationToolbox(getApplicationContext());
		latitude = locationToolbox.getLatitude();
		longitude = locationToolbox.getLongitude();
		coordinatesToAddress();

		location = new Location(latitude, longitude, address);
		place = new Place(location);

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
		startActivity(intent);
		finish();
	}

	public void addressClick(View v){
		Intent intent = new Intent(this, InputFormB.class);
		intent.putExtra("Start", start);
		intent.putExtra("Address", true);
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
						Intent intent = new Intent(getApplicationContext(), InputFormB.class);
						intent.putExtra("Start", start);
						intent.putExtra("GPS", true);
						intent.putExtra("Distance", true);
						startActivity(intent);
						dialog.cancel();
						saveLocation();
						locationToolbox.stopUsingGPS();
						finish();
					}
				})

				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(getApplicationContext(), NewProfileActivityB.class);
						intent.putExtra("Start", start);
						startActivity(intent);
						dialog.cancel();
						finish();
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	private void saveLocation() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();

		if(start)
			editor.putString("StartLocation", location.savingString());
		else
			editor.putString("EndLocation", location.savingString());

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
}
