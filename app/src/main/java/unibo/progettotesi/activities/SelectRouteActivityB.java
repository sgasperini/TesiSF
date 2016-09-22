package unibo.progettotesi.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import java.util.List;
import java.util.StringTokenizer;

import unibo.progettotesi.R;
import unibo.progettotesi.adapters.RoutesAdapter;
import unibo.progettotesi.model.Profile;
import unibo.progettotesi.model.Route;
import unibo.progettotesi.utilities.RouteFinder;
import unibo.progettotesi.utilities.Time;

public class SelectRouteActivityB extends AppCompatActivity {
	private RouteFinder routeFinder;
	private List<Route> routeList;
	private Profile profile;
	private boolean departureTime;
	private Time time;

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
	}

	public static void selectRoute(Activity activity, Route route) {
		//start next

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString("CurrentRoute", route.savingString());
		editor.commit();

		Intent intent = new Intent(activity, BusWaitingActivity.class);
		activity.startActivity(intent);

		activity.finish();
	}

	@Override
	protected void onDestroy() {
		if(routeList != null)
			routeList.clear();
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
