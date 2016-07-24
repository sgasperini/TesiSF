package unibo.progettotesi.utilities;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
import unibo.progettotesi.json.planSingleJourneyRequest.From;
import unibo.progettotesi.json.planSingleJourneyRequest.Request;
import unibo.progettotesi.json.planSingleJourneyRequest.To;
import unibo.progettotesi.json.planSingleJourneyResponse.Response;
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
	}

	public RouteFinder(Place startPlace, Place endPlace) {
		this.startPlace = startPlace;
		this.endPlace = endPlace;
		startTime = Time.now();
	}

	public List<Route> calculateRoutes(){	//DA RIVEDERE
		//getRoutesFromWeb();





		routes = new ArrayList<Route>();
		routes.add(new Route(startPlace, endPlace, startTime, new Time(startTime.getHour(), startTime.getMinute() + 50), new Stop(new Location(44.0, 11.0, "XYZciao"), 24, "fermino", "guarda che non esiste"), new Stop(new Location(44.527465, 11.0, "XYZgrazie"), 31, "fermata", "fittizia anch'essa")));
		routes.add(new Route(startPlace, endPlace, startTime, new Time(startTime.getHour() + 1, startTime.getMinute() + 20), new Stop(new Location(44.000012, 11.0, "XYZciao vicino"), 25, "fermino", "guarda che non esiste di fronte"), new Stop(new Location(44.527465, 11.0, "XYZgrazie"), 31, "fermata", "fittizia anch'essa")));
		routes.get(0).addLeg(new Leg(
				new Stop(new Location(44.0, 11.0, "XYZciao"), 6222, "fermino", "guarda che non esiste"),
				new Stop(new Location(44.527465, 11.0, "XYZgrazie"), 1045, "fermata", "fittizia anch'essa"),
				new Line("101", "Bologna Autostazione"),
				startTime, new Time(startTime.getHour(), startTime.getMinute() + 50)));
		routes.get(1).addLeg(new Leg(
				new Stop(new Location(44.000012, 11.0, "XYZciao vicino"), 25, "fermino", "guarda che non esiste di fronte"),
				new Stop(new Location(44.527465, 11.0, "XYZgrazie"), 31, "fermata", "fittizia anch'essa"),
				new Line("94", "Bazzano"),
				startTime, new Time(startTime.getHour() + 1, startTime.getMinute() + 20)));
		return routes;
	}

	public void getRoutesFromWeb(){
		String currentDate = new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime());
		List<String> transportTypes = new ArrayList<String>();
		transportTypes.add("TRANSIT");
		transportTypes.add("WALKING");

		Request r = new Request(new To(startPlace.getLocation().getLongitude() + "", startPlace.getLocation().getLatitude() + ""), "fastest", 5, startTime.toString12(), new From(endPlace.getLocation().getLongitude() + "", endPlace.getLocation().getLatitude() + ""), currentDate, transportTypes);

		Retrofit retrofit = new Retrofit.Builder()      //create the retrofit builder
				.baseUrl(Constants.PLAN_SINGLE_JOURNEY_BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())	//parse Gson string
				.build();

		RequestRoutes service = retrofit.create(RequestRoutes.class);

		Call<Response> queryResponseCall = service.requestRoutes(r);

		queryResponseCall.enqueue(new Callback<Response>(){

			@Override
			public void onResponse(retrofit2.Response<Response> response) {
				try{
					Log.wtf("TEST", response.body().walkingDuration + "min");
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

	public interface RequestRoutes {
		@POST("plansinglejourney?policyId=Trento")
		Call<Response> requestRoutes(@Body Request request);
	}
}
