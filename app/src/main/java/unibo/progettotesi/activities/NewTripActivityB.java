package unibo.progettotesi.activities;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
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

import unibo.progettotesi.R;
import unibo.progettotesi.adapters.ProfilesAdapter;
import unibo.progettotesi.model.Profile;
import unibo.progettotesi.utilities.Constants;
import unibo.progettotesi.utilities.LocationToolbox;
import unibo.progettotesi.utilities.Time;

public class NewTripActivityB extends AppCompatActivity {
	private boolean start;
	private ProfilesAdapter profilesAdapter;
	private Button timeOption;
	private Button timePickerButton;
	private boolean departureTime = true;
	private CountDownTimer timer;
	private double latitude;
	private double longitude;
	private LocationManager lm;

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
			Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			latitude = location.getLatitude();
			longitude = location.getLongitude();
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
						Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
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

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_trip_activity_b);

		setTitle("Seleziona Profilo");

		List<Profile> profileList = getProfiles();

		profilesAdapter = new ProfilesAdapter(this, R.layout.profile_b_list, profileList);

		((ListView) findViewById(R.id.listView2)).setAdapter(profilesAdapter);

		timePickerButton = (Button) findViewById(R.id.buttonTimePicker);
		timePickerButton.setText(Time.now().toString());

		timeOption = (Button) findViewById(R.id.buttonPlanningTime);

		timer = new CountDownTimer(1000000000, 1000) {

			public void onTick(long millisUntilFinished) {
				Calendar c = Calendar.getInstance();

				timePickerButton.setText(c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
			}
			public void onFinish() {

			}
		};
		timer.start();
	}

	private List<Profile> getProfiles() {
		List<Profile> output = new ArrayList<Profile>();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		int numProfiles = preferences.getInt("NumProfiles", 0);
		for (int i = 1; i <= numProfiles; i++) {
			output.add(Profile.getProfileFromString(preferences.getString("ProfileN_" + i, "")));
			Profile profile = output.get(output.size() - 1);
			output.get(output.size() - 1).setDistance((int) LocationToolbox.distance(latitude, profile.getStart().getLocation().getLatitude(), longitude, profile.getStart().getLocation().getLongitude(), 0.0, 0.0));
		}
		Collections.sort(output);
		return output;
	}

	public void openTimePicker(View v){
		TimePickerDialog timePickerDialog = new TimePickerDialog(this,
				new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
										  int minute) {
						timer.cancel();
						timePickerButton.setText(hourOfDay + ":" + minute);
					}
				}, Time.now().getHour(), Time.now().getMinute(), false);
		timePickerDialog.show();
	}

	public void changeTimeOption(View v){
		departureTime = !departureTime;
		timeOption.setText((departureTime ? "Partenza alle" : "Arrivo alle"));
	}

	public static void selectProfile(NewTripActivityB newTripActivityB, Profile profile){
		//leggere ad alta voce il nome
		//conferma
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(newTripActivityB);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString("CurrentProfile", profile.savingString());
		editor.commit();

		Intent intent = new Intent(newTripActivityB, SelectRouteActivityB.class);

		intent.putExtra("departureTime", newTripActivityB.departureTime);
		intent.putExtra("time", newTripActivityB.timePickerButton.getText().toString());

		newTripActivityB.startActivity(intent);

		newTripActivityB.finish();
	}

	@Override
	protected void onDestroy() {
		timer.cancel();
		super.onDestroy();
	}
}
