package unibo.progettotesi.utilities;

import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import unibo.progettotesi.activities.SelectRouteActivityB;
import unibo.progettotesi.json.planner.Response;
import unibo.progettotesi.model.Leg;
import unibo.progettotesi.model.Line;
import unibo.progettotesi.model.Location;
import unibo.progettotesi.model.Place;
import unibo.progettotesi.model.Route;
import unibo.progettotesi.model.Stop;

public class RouteFinder {
	private Place startPlace;
	private Place endPlace;
	private Time startTime;
	private List<Route> routes;

	public RouteFinder(Place startPlace, Place endPlace, Time startTime) {
		this.startPlace = startPlace;
		this.endPlace = endPlace;
		this.startTime = startTime;
		routes = new ArrayList<Route>();
	}

	public RouteFinder(Place startPlace, Place endPlace) {
		new RouteFinder(startPlace, endPlace, Time.now());
	}

	public void calculateRoutes(SelectRouteActivityB selectRouteActivityB){	//DA RIVEDERE
		getRoutesFromWeb(selectRouteActivityB);
	}

	public void getRoutesFromWeb(final SelectRouteActivityB selectRouteActivityB){
		Retrofit retrofit = new Retrofit.Builder()      //create the retrofit builder
				.baseUrl(Constants.PLANNING_BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())	//parse Gson string
				.build();

		RequestPlan service = retrofit.create(RequestPlan.class);
		String date = new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime());
		String time = unibo.progettotesi.utilities.Time.now().toString();

		URL url = null;
		HttpURLConnection connection = null;
		try {
			url = new URL(Constants.PLANNING_BASE_URL + "bologna/rest/plan");
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout( 60000 );
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.wtf("TIMEOUT", connection.getConnectTimeout() + "\t" + connection.getReadTimeout());

		Call<List<unibo.progettotesi.json.planner.Response>> queryResponseCall = service.requestPlan(
				startPlace.getLocation().getLatitude() + "," + startPlace.getLocation().getLongitude(),
				endPlace.getLocation().getLatitude() + "," + endPlace.getLocation().getLongitude(),
				date,
				time, "TRANSIT", "fastest", 5);

		queryResponseCall.enqueue(new Callback<List<unibo.progettotesi.json.planner.Response>>(){

			@Override
			public void onResponse(retrofit2.Response<List<unibo.progettotesi.json.planner.Response>> response) {
				try {
					if (response.body() != null && response.code() == 200){
						convertResponseToRoutes(selectRouteActivityB, response.body());
						Log.wtf("TEST PLANNING", response.body().get(0).getDuration() + "");
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Throwable t) {
				t.printStackTrace();
			}
		});
	}

	private void convertResponseToRoutes(SelectRouteActivityB selectRouteActivityB, List<Response> responses) {
		for (int i = 0; i < responses.size(); i++) {
			Route route = Route.getRouteFromPlanner(responses.get(i));
			if(route != null)
				routes.add(route);
		}
		selectRouteActivityB.setRouteList(routes);
	}


	public interface RequestPlan {
		@GET("bologna/rest/plan")
		Call<List<unibo.progettotesi.json.planner.Response>> requestPlan(@Query("from") String from,
																   @Query("to") String to,
																   @Query("date") String date,
																   @Query("departureTime") String departureTime,
																   @Query("transportType") String transportType,
																   @Query("routeType") String routeType,
																   @Query("numOfItn") int numOfItn);
	}
}
