package unibo.progettotesi.activities;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.List;
import java.util.Locale;

import unibo.progettotesi.R;
import unibo.progettotesi.model.Location;
import unibo.progettotesi.model.Place;
import unibo.progettotesi.model.Profile;

public class EditNameProfile extends AppCompatActivity {
	private int profileN;
	private EditText inputET;
	private Place place;
	private boolean address;
	private boolean departure;
	private TextToSpeech tts;
	private boolean voiceSupport;


	//analogous to inputForm
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_form_b);

		address = getIntent().getBooleanExtra("Address", false);

		inputET = (EditText) findViewById(R.id.inputEditText);
		if(!address){
			inputET.setHint("Nuovo nome");
			setTitle("Nuovo nome");
		}else{
			inputET.setHint("Nuovo indirizzo");
			setTitle("Nuovo indirizzo");
			departure = getIntent().getBooleanExtra("departure", false);
		}

		profileN = getIntent().getIntExtra("editProfileN", -1);

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
	}

	public void confirmInput(View view){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		Profile currentProfile = Profile.getProfileFromString(sharedPreferences.getString("ProfileN_" + profileN, ""));
		if(!address)
			currentProfile.setName(inputET.getText().toString());
		else{
			if(addressToCoordinates()) {
				if (departure)
					currentProfile.setStart(place);
				else
					currentProfile.setEnd(place);
			}
		}

		editor.putString("ProfileN_" + profileN, currentProfile.savingString());
		editor.commit();

		if(voiceSupport)
			tts.speak("Profilo modificato", TextToSpeech.QUEUE_FLUSH, null);

		EditActivityB.finishHandler.sendEmptyMessage(0);
		EditDeleteActivityB.finishHandler.sendEmptyMessage(0);
		finish();
	}

	private boolean addressToCoordinates() {
		findViewById(R.id.progressBar_inputForm).setVisibility(View.VISIBLE);
		findViewById(R.id.inputEditText).setVisibility(View.GONE);
		findViewById(R.id.confirm).setVisibility(View.GONE);
		findViewById(R.id.confirm).setClickable(false);

		Geocoder coder = new Geocoder(this);
		List<Address> address;
		boolean result;

		try {
			address = coder.getFromLocationName(inputET.getText().toString(), 1);
			if (address == null) {
				result = false;
			}
			Address location = address.get(0);
			location.getLatitude();
			location.getLongitude();

			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			place = new Place(new Location(latitude, longitude, inputET.getText().toString()));

			Log.wtf("COORDINATES FROM ADDRESS", latitude + " - " + longitude);
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			result = false;
		}

		findViewById(R.id.progressBar_inputForm).setVisibility(View.GONE);
		findViewById(R.id.inputEditText).setVisibility(View.VISIBLE);
		findViewById(R.id.confirm).setVisibility(View.VISIBLE);
		findViewById(R.id.confirm).setClickable(true);

		return result;
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
