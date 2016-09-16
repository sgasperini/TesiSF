package unibo.progettotesi.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import java.util.List;

import unibo.progettotesi.R;
import unibo.progettotesi.model.Profile;

public class EditActivityB extends Activity {
	private int profileN;
	private Profile currentProfile;
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_activity_b);

		profileN = getIntent().getIntExtra("editProfileN", -1);
		if(profileN == -1)
			finish();

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		currentProfile = Profile.getProfileFromString(sharedPreferences.getString("ProfileN_" + profileN, ""));
	}

	public void nameEditClick(View view){
		Intent intent = new Intent(this, EditNameProfile.class);
		intent.putExtra("editProfileN", profileN);
		startActivity(intent);

		finish();
	}

	public void departureClick(View view){

	}

	public void arrivalClick(View view){

	}


}
