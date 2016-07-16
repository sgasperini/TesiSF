package unibo.progettotesi.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ListView;

import java.util.List;

import unibo.progettotesi.R;
import unibo.progettotesi.adapters.RoutesAdapter;
import unibo.progettotesi.model.Profile;
import unibo.progettotesi.model.Route;
import unibo.progettotesi.utilities.Filler;
import unibo.progettotesi.utilities.RouteFinder;
import unibo.progettotesi.utilities.Time;

public class SelectRouteActivityB extends Activity {
	private RouteFinder routeFinder;
	private List<Route> routeList;
	private Profile profile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_route_activity_b);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		profile = Profile.getProfileFromString(sharedPreferences.getString("CurrentProfile", ""));

		routeFinder = new RouteFinder(profile.getStart(), profile.getEnd(), Time.now());
		routeList = routeFinder.calculateRoutes();

		RoutesAdapter routesAdapter = new RoutesAdapter(this, R.layout.route_list_b, routeList);
		((ListView) findViewById(R.id.listView3)).setAdapter(routesAdapter);

		Filler.fillRoute(findViewById(R.id.favoriteStops), routeList.get(0), this);
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
}
