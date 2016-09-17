package unibo.progettotesi.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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

		tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if(status != TextToSpeech.ERROR) {
					tts.setLanguage(Locale.getDefault());
				}
			}
		});
	}

	public void profilesManaging(View v){
		Intent intent = new Intent(this, ProfileManagingActivityB.class);
		//intent.putExtra("Start", true);

		if(!VoiceSupport.isTalkBackEnabled(this))
			Toast.makeText(MainActivity.this, "Gestione Profili", Toast.LENGTH_SHORT).show();
		tts.speak("Gestione profili", TextToSpeech.QUEUE_FLUSH, null);

		startActivity(intent);
	}

	public void newTrip(View v){
		Intent intent = new Intent(this, NewTripActivityB.class);

		if(!VoiceSupport.isTalkBackEnabled(this))
			Toast.makeText(MainActivity.this, "Nuovo Viaggio", Toast.LENGTH_SHORT).show();

		tts.speak("Nuovo viaggio", TextToSpeech.QUEUE_FLUSH, null);

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
