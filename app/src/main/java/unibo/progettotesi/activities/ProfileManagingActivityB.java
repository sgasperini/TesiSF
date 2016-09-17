package unibo.progettotesi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;

import unibo.progettotesi.R;
import unibo.progettotesi.utilities.VoiceSupport;

public class ProfileManagingActivityB extends AppCompatActivity {
	private TextToSpeech tts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_managing_activity_b);

		setTitle("Gestione Profili");

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

		if(!VoiceSupport.isTalkBackEnabled(this))
			Toast.makeText(this, "Nuovo Profilo", Toast.LENGTH_SHORT).show();
		tts.speak("Nuovo profilo, partenza, seleziona metodo immissione", TextToSpeech.QUEUE_FLUSH, null);

		startActivity(intent);
	}

	public void editProfile(View v){
		Intent intent = new Intent(this, EditDeleteActivityB.class);
		intent.putExtra("Edit", true);

		if(!VoiceSupport.isTalkBackEnabled(this))
			Toast.makeText(this, "Modifica profilo", Toast.LENGTH_SHORT).show();

		tts.speak("Modifica profilo, seleziona profilo da modificare", TextToSpeech.QUEUE_FLUSH, null);

		startActivity(intent);
	}

	public void deleteProfile(View v){
		Intent intent = new Intent(this, EditDeleteActivityB.class);

		if(!VoiceSupport.isTalkBackEnabled(this))
			Toast.makeText(this, "Elimina profilo", Toast.LENGTH_SHORT).show();

		tts.speak("Elimina profilo, seleziona profilo da eliminare", TextToSpeech.QUEUE_FLUSH, null);

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
