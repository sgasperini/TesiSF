package unibo.progettotesi.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import unibo.progettotesi.model.Location;
import unibo.progettotesi.R;

public class AddressProfileB extends AppCompatActivity {
	private boolean start;
	private EditText strAddress;
	private double latitude;
	private double longitude;
	private Location location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_profile_b);

		start = getIntent().getBooleanExtra("Start", false);
		latitude = 0;
		longitude = 0;
	}

	//set OnKeyListener sull'EditText

	public void setAddress(View v){
		//leggerlo ad alta voce, poi controllare che ritorni una posizione reale, chiedere quanto vuol camminare
		strAddress = (EditText) findViewById(R.id.addressEditText);
		addressToCoordinates();

		location = new Location(latitude, longitude, strAddress.getText().toString());

		savePreferences();

		Intent intent = new Intent(this, DistanceB.class);
		intent.putExtra("Start", start);
		startActivity(intent);
		finish();
	}

	private void savePreferences() {
		//scrivi location nelle prefernces
	}

	private void addressToCoordinates() {
		Geocoder coder = new Geocoder(this);
		List<Address> address;

		try {
			address = coder.getFromLocationName(strAddress.getText().toString(), 20, 44.341885, 11.205312, 44.622102, 11.668971);	//Marzabotto - Molinella
			if (address == null) {
				return;
			}
			Address location = address.get(0);
			location.getLatitude();
			location.getLongitude();

			latitude = location.getLatitude();
			longitude = location.getLongitude();

			Log.wtf("COORDINATES FROM ADDRESS", latitude + " - " + longitude);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
