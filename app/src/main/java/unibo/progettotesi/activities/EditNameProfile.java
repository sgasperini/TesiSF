package unibo.progettotesi.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

import unibo.progettotesi.R;
import unibo.progettotesi.model.Profile;

public class EditNameProfile extends Activity {
	private int profileN;
	private EditText inputET;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_form_b);

		inputET = (EditText) findViewById(R.id.inputEditText);
		inputET.setHint("Nuovo nome");
		setTitle("Nuovo nome");

		profileN = getIntent().getIntExtra("editProfileN", -1);
	}

	public void confirmInput(View view){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		Profile currentProfile = Profile.getProfileFromString(sharedPreferences.getString("ProfileN_" + profileN, ""));
		currentProfile.setName(inputET.getText().toString());

		editor.putString("ProfileN_" + profileN, currentProfile.savingString());
		editor.commit();

		finish();
	}
}
