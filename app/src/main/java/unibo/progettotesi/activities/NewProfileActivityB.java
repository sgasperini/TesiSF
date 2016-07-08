package unibo.progettotesi.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.List;
import java.util.Locale;

import unibo.progettotesi.R;
import unibo.progettotesi.model.Location;
import unibo.progettotesi.utilities.LocationToolbox;

public class NewProfileActivityB extends AppCompatActivity {
	private boolean start;
	private boolean gpsStart;
	private double latitude;
	private double longitude;
	private String address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_profile_activity_b);

		start = getIntent().getBooleanExtra("Start", false);
		gpsStart = getIntent().getBooleanExtra("GPS", false);
		//se è stato usato per la partenza il gps levalo
		if(gpsStart)
			findViewById(R.id.gps).setVisibility(View.GONE);
	}

	public void gpsClick(View v){
		// verificare sia attivo (accenderlo), prendere la posizione e mostrarla come indirizzo chiedendo se si vuol salvare come preferito, comunque sia chiedere la distanza a piedi, poi chiamare la nuova activity
		LocationToolbox locationToolbox = new LocationToolbox(getApplicationContext());
		latitude = locationToolbox.getLatitude();
		longitude = locationToolbox.getLongitude();
		coordinatesToAddress();

		Location location = new Location(latitude, longitude, address);
		//salvatela

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
		Intent intent = new Intent(this, AddressProfileB.class);
		intent.putExtra("Start", start);
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
						Intent intent = new Intent(getApplicationContext(), DistanceB.class);
						intent.putExtra("Start", start);
						intent.putExtra("GPS", true);
						startActivity(intent);
						dialog.cancel();
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
}
