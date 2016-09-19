package unibo.progettotesi.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Locale;

import unibo.progettotesi.R;
import unibo.progettotesi.utilities.VoiceSupport;

public class SettingsActivity extends AppCompatActivity {
	private boolean voiceSupport;
	private SharedPreferences sharedPreferences;
	private Switch switchV;
	private TextToSpeech tts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		setTitle("Impostazioni");

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		voiceSupport = sharedPreferences.getBoolean("VoiceSupport", true);
		switchV = (Switch) findViewById(R.id.voiceSwitch);
		switchV.setChecked(voiceSupport);
		switchV.setTextOn("ON ");
		switchV.setTextOff("OFF");

		if(voiceSupport)
			tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
				@Override
				public void onInit(int status) {
					if(status != TextToSpeech.ERROR) {
						tts.setLanguage(Locale.getDefault());
					}
				}
			});

		final Context context = this;
		switchV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putBoolean("VoiceSupport", isChecked);
				editor.commit();

				if(!VoiceSupport.isTalkBackEnabled(context))
					Toast.makeText(context, "Supporto vocale " + (isChecked ? "attivato" : "disattivato"), Toast.LENGTH_SHORT).show();

				if(voiceSupport)
					tts.speak("Supporto vocale " + (isChecked ? "attivato" : "disattivato"), TextToSpeech.QUEUE_FLUSH, null);
			}
		});

		findViewById(R.id.voiceSupportRL).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switchV.toggle();
			}
		});
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
