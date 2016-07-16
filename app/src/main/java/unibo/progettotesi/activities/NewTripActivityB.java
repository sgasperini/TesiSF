package unibo.progettotesi.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import unibo.progettotesi.R;
import unibo.progettotesi.adapters.FavoritesAdapter;
import unibo.progettotesi.adapters.ProfilesAdapter;
import unibo.progettotesi.model.Place;
import unibo.progettotesi.model.Profile;

public class NewTripActivityB extends AppCompatActivity {
	private boolean start;
	private ProfilesAdapter profilesAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_trip_activity_b);

		List<Profile> profileList = getProfiles();

		profilesAdapter = new ProfilesAdapter(this, R.layout.profile_b_list, profileList);

		((ListView) findViewById(R.id.listView2)).setAdapter(profilesAdapter);
	}

	private List<Profile> getProfiles() {
		List<Profile> output = new ArrayList<Profile>();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		int numProfiles = preferences.getInt("NumProfiles", 0);
		for (int i = 1; i <= numProfiles; i++) {
			output.add(Profile.getProfileFromString(preferences.getString("ProfileN_" + i, "")));
		}
		return output;
	}

	public static void selectProfile(NewTripActivityB newTripActivityB, Profile profile){
		//leggere ad alta voce il nome
		//conferma
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(newTripActivityB);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString("CurrentProfile", profile.savingString());
		editor.commit();

		Intent intent = new Intent(newTripActivityB, SelectRouteActivityB.class);
		newTripActivityB.startActivity(intent);

		newTripActivityB.finish();
	}
}
