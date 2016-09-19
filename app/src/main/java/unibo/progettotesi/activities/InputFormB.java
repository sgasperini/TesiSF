package unibo.progettotesi.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import unibo.progettotesi.R;
import unibo.progettotesi.model.Location;
import unibo.progettotesi.model.Place;
import unibo.progettotesi.utilities.Constants;
import unibo.progettotesi.utilities.VoiceSupport;

public class InputFormB extends AppCompatActivity {
	private boolean start;
	private boolean gps;
	private boolean address;
	private boolean name;
	private boolean favorite;
	private EditText editText;
	private double latitude;
	private double longitude;
	private Location location;
	private TextToSpeech tts;
	private boolean voiceSupport;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_form_b);

		start = getIntent().getBooleanExtra("Start", false);
		gps = getIntent().getBooleanExtra("GPS", false);
		address = getIntent().getBooleanExtra("Address", false);
		name = getIntent().getBooleanExtra("Name", false);
		favorite = getIntent().getBooleanExtra("Favorite", false);



		editText = (EditText) findViewById(R.id.inputEditText);
		editText.setTextSize(36);

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

		boolean talkBack = VoiceSupport.isTalkBackEnabled(this);
		if(address){
			latitude = 0;
			longitude = 0;
			editText.setHint(Constants.ADDRESS_INPUT_HINT);
			this.setTitle("Indirizzo");
		}else if(name){
			if(favorite) {
				editText.setHint(Constants.NAME_FAVORITE_INPUT_HINT);
			}else{
				editText.setHint(Constants.NAME_PROFILE_INPUT_HINT);
			}
			this.setTitle("Nome");
		}
	}

	public void confirmInput(View v){
		if(address){
			setAddress(1);
		}else if(name){
			setName();
		}
	}

	private void setName() {
		if(!favorite) {
			NewProfileActivityB.saveProfile(this, editText.getText().toString());
			if(voiceSupport)
				tts.speak("Profilo salvato", TextToSpeech.QUEUE_FLUSH, null);
			if(!VoiceSupport.isTalkBackEnabled(this))
				Toast.makeText(InputFormB.this, "Profilo Salvato", Toast.LENGTH_SHORT).show();
			finish();
		}else {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = preferences.edit();

			int numFavorites = preferences.getInt("NumFavorites", 0) + 1;
			Place place;
			if (start)
				place = Place.getPlaceFromString(preferences.getString("StartTempPlace", ""));
			else
				place = Place.getPlaceFromString(preferences.getString("EndTempPlace", ""));
			place.setName(editText.getText().toString());

			editor.putString("FavoriteN_" + numFavorites, place.savingStringFavorite());
			editor.putInt("NumFavorites", numFavorites);

			if(voiceSupport)
				tts.speak(editText.getText().toString() + " salvato", TextToSpeech.QUEUE_FLUSH, null);
			if(!VoiceSupport.isTalkBackEnabled(this))
				Toast.makeText(InputFormB.this, editText.getText().toString() + " salvato", Toast.LENGTH_SHORT).show();

			editor.commit();

			continueProfileCreation(null, false);
		}
	}

	//set OnKeyListener sull'EditText

	private void setAddress(int cont){
		//leggerlo ad alta voce, poi controllare che ritorni una posizione reale

		if(addressToCoordinates()) {

			if(voiceSupport)
				tts.speak("Inserito " + editText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

			location = new Location(latitude, longitude, editText.getText().toString());

			saveLocation();

			askFavorite();

		}else {
			if (cont < 4)
				setAddress(cont + 1);
			else {
				if(!VoiceSupport.isTalkBackEnabled(this))
					Toast.makeText(InputFormB.this, "Indirizzo non trovato", Toast.LENGTH_SHORT).show();
				if(voiceSupport)
					tts.speak("Indirizzo non trovato", TextToSpeech.QUEUE_FLUSH, null);
			}
		}
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
			address = coder.getFromLocationName(editText.getText().toString(), 1);
			if (address == null) {
				result = false;
			}
			Address location = address.get(0);
			location.getLatitude();
			location.getLongitude();

			latitude = location.getLatitude();
			longitude = location.getLongitude();

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

	private void saveLocation() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();

		Place place = new Place(location);
		if(start)
			editor.putString("StartTempPlace", place.savingString());
		else
			editor.putString("EndTempPlace", place.savingString());

		editor.commit();
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
						startThisName(true);
						continueProfileCreation(dialog, true);
					}
				})

				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						continueProfileCreation(dialog, false);
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	private void startThisName(boolean forFavorite) {
		Intent intent = new Intent(this, InputFormB.class);
		intent.putExtra("Start", start);
		intent.putExtra("Name", true);
		intent.putExtra("Favorite", forFavorite);
		if(voiceSupport)
			if(!VoiceSupport.isTalkBackEnabled(getApplicationContext())) {
				if (forFavorite)
					tts.speak("Immettere nome preferito", TextToSpeech.QUEUE_FLUSH, null);
				else
					tts.speak("Immettere nome profilo", TextToSpeech.QUEUE_FLUSH, null);
			}
		startActivity(intent);
		finish();
	}

	private void continueProfileCreation(DialogInterface dialog, boolean savingFavorite){
		if(start && !savingFavorite) {
			//NewProfileActivityB.finishHandler.sendEmptyMessage(0);

			Intent intent = new Intent(getApplicationContext(), NewProfileActivityB.class);
			intent.putExtra("Start", !start);
			intent.putExtra("GPS", gps);
			startActivity(intent);
		}else if(!savingFavorite){
			startThisName(false);
		}
		if(dialog != null)
			dialog.cancel();

		finish();
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