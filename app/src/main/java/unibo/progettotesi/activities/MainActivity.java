package unibo.progettotesi.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;

import unibo.progettotesi.R;
import unibo.progettotesi.model.Route;
import unibo.progettotesi.utilities.VoiceSupport;

public class MainActivity extends AppCompatActivity {
	private String routeS;
	private String stopS;
	private TextToSpeech tts;
	private SharedPreferences sharedPreferences;
	private boolean voiceSupport;
	private boolean oldVoiceSupport;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setTitle("Home");

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		routeS = sharedPreferences.getString("CurrentRoute", "");
		stopS = sharedPreferences.getString("LastStop", "");
		Log.wtf("CrashDetector route", routeS);
		Log.wtf("CrashDetector stop", stopS);

		if(!routeS.equals("")){
			if(!stopS.equals("")){
				Intent intent = new Intent(this, OnTheGoActivity.class);
				intent.putExtra("LastStop", stopS);
				startActivity(intent);
			}else{
				Intent intent = new Intent(this, BusWaitingActivity.class);
				startActivity(intent);
			}
		}

		voiceSupport = sharedPreferences.getBoolean("VoiceSupport", true);
		oldVoiceSupport = voiceSupport;

		if(voiceSupport)
			tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
				@Override
				public void onInit(int status) {
					if(status != TextToSpeech.ERROR) {
						tts.setLanguage(Locale.getDefault());
					}
				}
			});

		requestVoiceSetting();
	}

	@Override
	protected void onResume() {
		voiceSupport = sharedPreferences.getBoolean("VoiceSupport", true);
		if(voiceSupport && !oldVoiceSupport) {
			tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
				@Override
				public void onInit(int status) {
					if (status != TextToSpeech.ERROR) {
						tts.setLanguage(Locale.getDefault());
					}
				}
			});
			oldVoiceSupport = voiceSupport;
		}

		super.onResume();
	}

	private void requestVoiceSetting() {
		if(sharedPreferences.getBoolean("FirstAccess", true)) {
			final SharedPreferences.Editor editor = sharedPreferences.edit();
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			if (!VoiceSupport.isTalkBackEnabled(this)) {
				tts.speak("Attivare il supporto vocale?", TextToSpeech.QUEUE_FLUSH, null);
			}
			alertDialogBuilder
				.setTitle("Voce")
				.setIcon(R.mipmap.ic_launcher)
				.setMessage("Attivare il supporto vocale?\n(sarà possibile modificare l'opzione in seguito)")
				.setCancelable(false)
				.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						editor.putBoolean("FirstAccess", false);
						editor.putBoolean("VoiceSupport", true);
						editor.commit();
						dialog.cancel();
						voiceSupport = true;
					}
				})

				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						editor.putBoolean("FirstAccess", false);
						editor.putBoolean("VoiceSupport", false);
						editor.commit();
						dialog.cancel();
						voiceSupport = false;
					}
				});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
	}

	public void profilesManaging(View v){
		Intent intent = new Intent(this, ProfileManagingActivityB.class);
		//intent.putExtra("Start", true);

		if(!VoiceSupport.isTalkBackEnabled(this))
			Toast.makeText(MainActivity.this, "Gestione Profili", Toast.LENGTH_SHORT).show();
		if(voiceSupport)
			tts.speak("Gestione profili", TextToSpeech.QUEUE_FLUSH, null);

		startActivity(intent);
	}

	public void newTrip(View v){
		Intent intent = new Intent(this, NewTripActivityB.class);

		if(!VoiceSupport.isTalkBackEnabled(this))
			Toast.makeText(MainActivity.this, "Nuovo Viaggio", Toast.LENGTH_SHORT).show();

		if(voiceSupport)
			tts.speak("Nuovo viaggio", TextToSpeech.QUEUE_FLUSH, null);

		startActivity(intent);
	}

	public void openSettings(View v){
		Intent intent = new Intent(this, SettingsActivity.class);

		if(!VoiceSupport.isTalkBackEnabled(this))
			Toast.makeText(MainActivity.this, "Impostazioni", Toast.LENGTH_SHORT).show();

		if(voiceSupport)
			tts.speak("Impostazioni", TextToSpeech.QUEUE_FLUSH, null);

		startActivity(intent);
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
