package unibo.progettotesi.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import java.util.Locale;

import unibo.progettotesi.R;
import unibo.progettotesi.utilities.ActivityRecognitionService;
import unibo.progettotesi.utilities.AuthenticationHandler;
import unibo.progettotesi.utilities.VoiceSupport;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	//public static Handler notificationHandler;
	private String routeS;
	private String stopS;
	private TextToSpeech tts;
	private static SharedPreferences sharedPreferences;
	private static SharedPreferences.Editor editor;
	private boolean voiceSupport;
	private boolean oldVoiceSupport;
	private GoogleApiClient apiClient;
	public static Handler startActivityHandlerBusWaiting;
	public static Handler endActivityHandlerBusWaiting;
	private boolean calledByBusWaiting;
	public static Handler startActivityHandlerOnTheGo;
	public static Handler endActivityHandlerOnTheGo;
	private boolean calledByOnTheGo;
	private PendingIntent pendingIntentActivityUpdates;
	public static Handler finishHandler;
	private boolean accountCreated;
	private boolean loggedIn = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setTitle("Home");

		//retrieving from preferences information about possible previous failure
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		routeS = sharedPreferences.getString("CurrentRoute", "");
		stopS = sharedPreferences.getString("LastStop", "");
		accountCreated = sharedPreferences.getBoolean("accountCreated", false);
		loggedIn = sharedPreferences.getBoolean("loggedIn", false);

		Log.wtf("CrashDetector route", routeS);
		Log.wtf("CrashDetector stop", stopS);

		//iF crash detected in otg, lunch OTG with the last known stop, else bus waiting, else stay in main
		if (!routeS.equals("")) {
			if (!stopS.equals("")) {
				Intent intent = new Intent(this, OnTheGoActivity.class);
				intent.putExtra("LastStop", stopS);
				startActivity(intent);
			} else {
				Intent intent = new Intent(this, BusWaitingActivity.class);
				startActivity(intent);
			}
		}

		//retrieve whether or not voice support should be active
		voiceSupport = sharedPreferences.getBoolean("VoiceSupport", true);
		oldVoiceSupport = voiceSupport;

		if (voiceSupport)
			tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
				@Override
				public void onInit(int status) {
					if (status != TextToSpeech.ERROR) {
						tts.setLanguage(Locale.getDefault());
					}
				}
			});

		editor = sharedPreferences.edit();
		requestVoiceSetting();

		//apri api e handlers per getire la activity recognition (car, walking etc)
		apiClient = new GoogleApiClient.Builder(this)
				.addApi(ActivityRecognition.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();

		startActivityHandlerBusWaiting = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Log.wtf("START RECOGNITION", "main");
				if (!apiClient.isConnected()) {
					calledByBusWaiting = true;
					calledByOnTheGo = false;
					apiClient.connect();
				}
			}
		};
		endActivityHandlerBusWaiting = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Log.wtf("END RECOGNITION", "main");
				removeActivityRecognitionUpdates();
			}
		};

		startActivityHandlerOnTheGo = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Log.wtf("START RECOGNITION", "main");
				if (!apiClient.isConnected()) {
					calledByBusWaiting = false;
					calledByOnTheGo = true;
					apiClient.connect();
				}
			}
		};
		endActivityHandlerOnTheGo = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Log.wtf("END RECOGNITION", "main");
				removeActivityRecognitionUpdates();
			}
		};

		finishHandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				finish();
			}
		};

		/*notificationHandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Log.wtf("Notification RECOGNITION", "main");

				newTrip(null);
			}
		};*/

		/*
        //logs in to get permissions for authentication, have to come back on your own after
        if (!loggedIn) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://dev.smartcommunitylab.it" +
                    "/aac/eauth/authorize?response_type=token&redirect_uri=https%3A%2F%2Fdev.smartcommunitylab.it" +
                    "%2Fprofileservice%2Fo2c.html&realm=your-realms&client_id=8b562674-006b-4e87-a2c2-0c4028dac41f" +
                    "&scope=profile.basicprofile.me%2Cpersonal.mobility.read%2Cpersonal.mobility.write&state=personaloauth2schema"));
            startActivity(browserIntent);
            Toast.makeText(MainActivity.this, "Effettua l'accesso con Facebook o Google", Toast.LENGTH_SHORT).show();
            loggedIn = true;
            editor.putBoolean("loggedIn", loggedIn);
            editor.commit();
        }*/

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
		//if the app is opened for the first time, it asks if voice support is wanted with a dialog
		if(sharedPreferences.getBoolean("FirstAccess", true)) {
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

	//start profiles handling
	public void profilesManaging(View v){
		//if the app is opened for the first time, the account is created
		/*
		if (!accountCreated)
			AuthenticationHandler.createAccount();
		*/
		Intent intent = new Intent(this, ProfileManagingActivityB.class);
		//intent.putExtra("Start", true);

		if(!VoiceSupport.isTalkBackEnabled(this))
			Toast.makeText(MainActivity.this, "Gestione Profili", Toast.LENGTH_SHORT).show();
		if(voiceSupport)
			tts.speak("Gestione profili", TextToSpeech.QUEUE_FLUSH, null);

		startActivity(intent);
	}

	//start new trip
	public void newTrip(View v){
		//if the app is opened for the first time, the account is created
		/*
		if (!accountCreated)
			AuthenticationHandler.createAccount();
		*/
		Intent intent = new Intent(this, NewTripActivityB.class);

		if(!VoiceSupport.isTalkBackEnabled(this))
			Toast.makeText(MainActivity.this, "Nuovo Viaggio", Toast.LENGTH_SHORT).show();

		if(voiceSupport)
			tts.speak("Nuovo viaggio", TextToSpeech.QUEUE_FLUSH, null);

		startActivity(intent);
	}

	//start settings
	public void openSettings(View v){
		//if the app is opened for the first time, the account is created
		/*
		if (!accountCreated)
			AuthenticationHandler.createAccount();
		*/
		Intent intent = new Intent(this, SettingsActivity.class);

		if(!VoiceSupport.isTalkBackEnabled(this))
			Toast.makeText(MainActivity.this, "Impostazioni", Toast.LENGTH_SHORT).show();

		if(voiceSupport)
			tts.speak("Impostazioni", TextToSpeech.QUEUE_FLUSH, null);

		startActivity(intent);
	}

	//always shut down text to speech and disconnect from the google api
	@Override
	protected void onDestroy() {
		if(tts !=null){
			while(tts.isSpeaking()){}
			tts.stop();
			tts.shutdown();
		}
		editor.putBoolean("loggedIn", false);
		editor.commit();

		if(apiClient.isConnected())
			apiClient.disconnect();

		super.onDestroy();
	}

	//launch the recognition activity with 1s updates
	@Override
	public void onConnected(@Nullable Bundle bundle) {
		try {
			Log.wtf("ACTIVITY RECOGNITION", "connesso");
			Intent intent = new Intent(this, ActivityRecognitionService.class);
			pendingIntentActivityUpdates = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(apiClient, 1000, pendingIntentActivityUpdates);
			if (calledByBusWaiting)
				ActivityRecognitionService.activityHandlerBusWaiting.sendEmptyMessage(0);
			if (calledByOnTheGo)
				ActivityRecognitionService.activityHandlerOnTheGo.sendEmptyMessage(0);
		}catch(Exception e){
			Log.wtf("ACTIVITY RECOGNITION PROBLEM !!!!", e.getMessage());
		}
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.wtf("ACTIVITY RECOGNITION", "connessione sospesa");
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		Log.wtf("ACTIVITY RECOGNITION", "connessione fallita" + connectionResult.getErrorMessage());
	}

	private void removeActivityRecognitionUpdates(){
		try{
			ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(apiClient, pendingIntentActivityUpdates);
			if(apiClient.isConnected())
				apiClient.disconnect();
			Log.wtf("ACTIVITY RECOGNITION", "rimossi updates");
		}catch (Exception e){
			Log.wtf("ACTIVITY RECOGNITION PROBLEM", "rimozione updates" + e.getMessage() + "\n\t\t\t\t\ttipicamente già rimossi");
		}
	}

	public static void setAccountCreated(boolean b) {
		editor = sharedPreferences.edit();
		editor.putBoolean("accountCreated", b);
	}
}
