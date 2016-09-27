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
import android.widget.ListView;

import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import unibo.progettotesi.R;
import unibo.progettotesi.adapters.RoutesAdapter;
import unibo.progettotesi.model.Profile;
import unibo.progettotesi.model.Route;
import unibo.progettotesi.utilities.RouteFinder;
import unibo.progettotesi.utilities.Time;
import unibo.progettotesi.utilities.VoiceSupport;

public class SelectRouteActivityB extends AppCompatActivity {
	private RouteFinder routeFinder;
	private List<Route> routeList;
	private Profile profile;
	private boolean departureTime;
	private Time time;
	public static Handler finishHandler;
	private TextToSpeech tts;
	private boolean voiceSupport;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_route_activity_b);

		setTitle("Seleziona Percorso");

		departureTime = getIntent().getBooleanExtra("departureTime", true);
		String timeS = getIntent().getStringExtra("time");
		StringTokenizer stringTokenizer = new StringTokenizer(timeS, ":");
		time = new Time(Integer.parseInt(stringTokenizer.nextToken()), Integer.parseInt(stringTokenizer.nextToken()));

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		profile = Profile.getProfileFromString(sharedPreferences.getString("CurrentProfile", ""));

		routeFinder = new RouteFinder(profile.getStart(), profile.getEnd(), time, departureTime);
		/*routeList = */routeFinder.calculateRoutes(this);

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

	public static void selectRoute(SelectRouteActivityB activity, Route route) {
		//start next

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString("CurrentRoute", route.savingString());
		editor.commit();

		if(sharedPreferences.getBoolean("VoiceSupport", false))
			if(!VoiceSupport.isTalkBackEnabled(activity)){
				activity.tts.speak("Selezionato percorso da: " + route.getStartStop().getName() + " alle " + route.getStartTime().toString() + ", a " + route.getEndStop().getName() + " alle " + route.getEndTime().toString(), TextToSpeech.QUEUE_FLUSH, null);
			}

		Intent intent = new Intent(activity, BusWaitingActivity.class);
		activity.startActivity(intent);

		//activity.finish();
	}

	@Override
	protected void onDestroy() {
		if(routeList != null)
			routeList.clear();

		if(tts !=null){
			while(tts.isSpeaking()){}
			tts.stop();
			tts.shutdown();
		}

		super.onDestroy();
	}

	public void setRouteList(List<Route> routeList){
		this.routeList = routeList;
		findViewById(R.id.progressBar_selectRoute).setVisibility(View.GONE);
		//Filler.fillRoute(findViewById(R.id.favoriteStops), routeList.get(0), this);

		RoutesAdapter routesAdapter = new RoutesAdapter(this, R.layout.route_list_b, routeList/*.subList(1, routeList.size())*/);
		((ListView) findViewById(R.id.listView3)).setAdapter(routesAdapter);
	}
}
