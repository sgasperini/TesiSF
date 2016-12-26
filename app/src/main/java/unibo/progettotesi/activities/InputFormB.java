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
	private boolean singleTrip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_form_b);

		//it needs to know where it is in the process, what process it is, if it's a favorite
		//or a profile, single trip or not
		start = getIntent().getBooleanExtra("Start", false);
		gps = getIntent().getBooleanExtra("GPS", false);
		address = getIntent().getBooleanExtra("Address", false);
		name = getIntent().getBooleanExtra("Name", false);
		favorite = getIntent().getBooleanExtra("Favorite", false);
		singleTrip = getIntent().getBooleanExtra("singleTrip", false);

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
		//if not favorite, launches the static method to save the profile with the name inserted
		if(!favorite) {
			NewProfileActivityB.saveProfile(this, editText.getText().toString());
			if(voiceSupport)
				tts.speak("Profilo salvato", TextToSpeech.QUEUE_FLUSH, null);
			if(!VoiceSupport.isTalkBackEnabled(this))
				Toast.makeText(InputFormB.this, "Profilo Salvato", Toast.LENGTH_SHORT).show();
			finish();
		}else {
			//if it's favorite, saves among the favorites
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

	private void setAddress(int cont){
		//tries to find coordinates for the address inserted

		if(addressToCoordinatesBologna()) {
			//if successful, it saves the location, and then distingueses for single or regular trip
			if(voiceSupport)
				tts.speak("Inserito " + editText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

			location = new Location(latitude, longitude, editText.getText().toString());

			saveLocation();

			if(!singleTrip)
				askFavorite();
			else {
				NewProfileActivityB.saveSingleTripProfile(this);
				finish();
				NewProfileActivityB.finishHandlerEnd.sendEmptyMessage(0);
			}
		}else {
			//if not successful, it tries two more times to avoid network problems
			if (cont < 2)
				setAddress(cont + 1);
			else {
				//if the problems are with the inserted address, it communicates to the user
				if(!VoiceSupport.isTalkBackEnabled(this))
					Toast.makeText(InputFormB.this, "Indirizzo non trovato o fuori dall'area supportata.\n Reinserire.", Toast.LENGTH_SHORT).show();
				if(voiceSupport)
					tts.speak("Indirizzo non trovato o fuori dall'area supportata. Reinserire.", TextToSpeech.QUEUE_FLUSH, null);
				editText.setText("");
			}
		}
	}

	private boolean addressToCoordinatesBologna() {
		findViewById(R.id.progressBar_inputForm).setVisibility(View.VISIBLE);
		findViewById(R.id.inputEditText).setVisibility(View.GONE);
		findViewById(R.id.confirm).setVisibility(View.GONE);
		findViewById(R.id.confirm).setClickable(false);

		Geocoder coder = new Geocoder(this);
		List<Address> address;
		boolean result;

		try {
			address = coder.getFromLocationName(editText.getText().toString(), 1);
			if (address == null || address.size()==0) {
				result = false;
			}
			Address location = address.get(0);
			location.getLatitude();
			location.getLongitude();

			latitude = location.getLatitude();
			longitude = location.getLongitude();

			Log.wtf("COORDINATES FROM ADDRESS", latitude + " - " + longitude);
			if (addressInBologna(latitude, longitude)){
				result = true;
			} else
				result = false;


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

	private boolean addressInBologna(double latitude, double longitude) {
		return ((latitude>=44.252448 && latitude<=44.609696) &&
				(longitude>=11.017831 && longitude<=11.900342));
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

	//it can start itself if it needs to save the name of the profile or the name of a favorite if wanted by the user
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