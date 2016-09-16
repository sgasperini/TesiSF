package unibo.progettotesi.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;

import unibo.progettotesi.R;

public class ProfileManagingActivityB extends Activity {
	private TextToSpeech tts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_managing_activity_b);

		tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if(status != TextToSpeech.ERROR) {
					tts.setLanguage(Locale.getDefault());
				}
			}
		});
	}

	public void newProfile(View v){
		Intent intent = new Intent(this, NewProfileActivityB.class);
		intent.putExtra("Start", true);

		Toast.makeText(this, "Nuovo Profilo", Toast.LENGTH_SHORT).show();
		tts.speak("Nuovo profilo", TextToSpeech.QUEUE_FLUSH, null);

		startActivity(intent);
	}

	public void editProfile(View v){
		Intent intent = new Intent(this, EditDeleteActivityB.class);
		intent.putExtra("Edit", true);

		Toast.makeText(this, "Modifica profilo", Toast.LENGTH_SHORT).show();

		tts.speak("Modifica profilo", TextToSpeech.QUEUE_FLUSH, null);

		startActivity(intent);
	}

	public void deleteProfile(View v){
		Intent intent = new Intent(this, EditDeleteActivityB.class);

		Toast.makeText(this, "Elimina profilo", Toast.LENGTH_SHORT).show();

		tts.speak("Elimina profilo", TextToSpeech.QUEUE_FLUSH, null);

		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		if(tts !=null){
			tts.stop();
			tts.shutdown();
		}

		super.onDestroy();
	}
}
