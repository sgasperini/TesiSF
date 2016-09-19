package unibo.progettotesi.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import unibo.progettotesi.R;
import unibo.progettotesi.model.Profile;
import unibo.progettotesi.utilities.VoiceSupport;

public class EditActivityB extends AppCompatActivity {
	private int profileN;
	private Profile currentProfile;
	private TextToSpeech tts;
	private SharedPreferences sharedPreferences;
	private boolean voiceSupport;
	public static Handler finishHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_activity_b);

		setTitle("Modifica");

		profileN = getIntent().getIntExtra("editProfileN", -1);
		if(profileN == -1)
			finish();

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		currentProfile = Profile.getProfileFromString(sharedPreferences.getString("ProfileN_" + profileN, ""));

		voiceSupport = sharedPreferences.getBoolean("VoiceSupport", true);

		if(voiceSupport)
			tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
				@Override
				public void onInit(int status) {
					if(status != TextToSpeech.ERROR) {
						tts.setLanguage(Locale.getDefault());
					}
				}
			});

		finishHandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				finish();
			}
		};
	}

	public void nameEditClick(View view){
		Intent intent = new Intent(this, EditNameProfile.class);
		intent.putExtra("editProfileN", profileN);
		if(!VoiceSupport.isTalkBackEnabled(this))
			Toast.makeText(this, "Modifica Nome", Toast.LENGTH_SHORT).show();
		if(voiceSupport)
			tts.speak("Inserisci nuovo nome", TextToSpeech.QUEUE_FLUSH, null);
		startActivity(intent);

		//finish();
	}

	public void departureClick(View view){
		Intent intent = new Intent(this, NewProfileActivityB.class);
		intent.putExtra("editProfileN", profileN);
		intent.putExtra("departure", true);
		if(!VoiceSupport.isTalkBackEnabled(this))
			Toast.makeText(this, "Modifica Partenza", Toast.LENGTH_SHORT).show();
		if(voiceSupport)
			tts.speak("Modifica partenza, selezionare metodo immissione", TextToSpeech.QUEUE_FLUSH, null);
		startActivity(intent);

		//finish();
	}

	public void arrivalClick(View view){
		Intent intent = new Intent(this, NewProfileActivityB.class);
		intent.putExtra("editProfileN", profileN);
		intent.putExtra("departure", false);
		if(!VoiceSupport.isTalkBackEnabled(this))
			Toast.makeText(this, "Modifica Destinazione", Toast.LENGTH_SHORT).show();
		if(voiceSupport)
			tts.speak("Modifica destinazione, selezionare metodo immissione", TextToSpeech.QUEUE_FLUSH, null);
		startActivity(intent);

		//finish();
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
