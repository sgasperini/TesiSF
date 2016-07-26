package unibo.progettotesi.utilities;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import unibo.progettotesi.activities.OnTheGoActivity;
import unibo.progettotesi.json.getNextTripsRequest.Date;
import unibo.progettotesi.json.getNextTripsRequest.Request;
import unibo.progettotesi.json.getNextTripsRequest.Time;
import unibo.progettotesi.json.getNextTripsResponse.Response;
import unibo.progettotesi.json.planner.From;
import unibo.progettotesi.json.planner.StopId;
import unibo.progettotesi.json.planner.To;
import unibo.progettotesi.model.Leg;
import unibo.progettotesi.model.Line;
import unibo.progettotesi.model.Location;
import unibo.progettotesi.model.Stop;

public class RealTimeTracker {
	private OnTheGoActivity onTheGoActivity;
	private Leg currentLeg;
	private boolean antiLoop = false;


	public static void setDistanceTo(TextView textView, Stop stop){
		textView.setText(500 + "");
	}

	public static void setBusTime(TextView textView, Stop stop, Line line, unibo.progettotesi.utilities.Time time){
		textView.setText(time.toString());
	}

	public void getStopsFromWeb(final OnTheGoActivity onTheGoActivity, final Leg currentLeg, final boolean first){
		this.onTheGoActivity = onTheGoActivity;
		this.currentLeg = currentLeg;

		Retrofit retrofit = new Retrofit.Builder()      //create the retrofit builder
				.baseUrl(Constants.GET_NEXT_TRIPS_BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())	//parse Gson string
				.build();

		RequestStops service = retrofit.create(RequestStops.class);

		Calendar calendar = Calendar.getInstance();

		Time time = new Time(currentLeg.getStartTime().getHour(), currentLeg.getStartTime().getMinute(), 0);
		Date date = new Date(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR),
				calendar.get(Calendar.DAY_OF_MONTH));

		Call<Response> queryResponseCall = service.requestStops(new Request(
				date, (first ? 0 : 1), currentLeg.getLine().getName(),
				currentLeg.getStartStop().getCode() + "", 1000, currentLeg.getEndStop().getCode() + "",
				time));

		queryResponseCall.enqueue(new Callback<Response>(){

			@Override
			public void onResponse(retrofit2.Response<Response> response) {
				try{
					if(response.body() != null && response.code() == 200){
						boolean found = false;
						for (int i = 0; i < response.body().trips.size() && !found; i++) {
							if(response.body().trips.get(i).tripId.equals(currentLeg.getLine().getTripID())) {
								stopToInterStopConverter(response.body().trips.get(i).stops);
								found = true;
							}
						}
						if(!found && !antiLoop){
							getStopsFromWeb(onTheGoActivity, currentLeg, !first);
							antiLoop = true;
						}
					}else if(response.body() == null && response.code() == 200){
						if(!antiLoop){
							getStopsFromWeb(onTheGoActivity, currentLeg, !first);
							antiLoop = true;
						}
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

	private void stopToInterStopConverter(List<unibo.progettotesi.json.getNextTripsResponse.Stop> stopsR){
		String startStopCode = "" + currentLeg.getStartStop().getCode();
		String endStopCode = "" + currentLeg.getEndStop().getCode();
		boolean journeyStops = false;
		boolean last = false;
		List<Stop> stopsOutput = new ArrayList<Stop>();
		for (int i = 0; i < stopsR.size(); i++) {
			if(!journeyStops){
				if(startStopCode.equals(stopsR.get(i).stopId)) {
					journeyStops = true;
				}
			}else {
				if (endStopCode.equals(stopsR.get(i).stopId)) {
					journeyStops = false;
					last = true;
				}
			}
			if(journeyStops || last){
				unibo.progettotesi.json.getNextTripsResponse.Stop stopR = stopsR.get(i);
				stopsOutput.add(new Stop(new Location(stopR.stopLat, stopR.stopLon, coordinatesToAddress(stopR.stopLat, stopR.stopLon)), Integer.parseInt(stopR.stopId), stopR.stopName, new unibo.progettotesi.utilities.Time(stopR.departureTime)));
				if(last){
					last = false;
					i = stopsR.size();
				}
			}
		}

		currentLeg.setInterStops(stopsOutput);
		onTheGoActivity.setNewLeg(currentLeg);
	}

	public static void calculateDistances(OnTheGoActivity onTheGoActivity, android.location.Location location, Location locationS0, Location locationS1) {
		calculateDistance(onTheGoActivity, location, locationS0, true);
		calculateDistance(onTheGoActivity, location, locationS1, false);
	}

	private static void calculateDistance(final OnTheGoActivity onTheGoActivity, android.location.Location location, Location locationS, final boolean next) {
		Retrofit retrofit = new Retrofit.Builder()      //create the retrofit builder
				.baseUrl(Constants.PLANNING_BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())	//parse Gson string
				.build();

		RequestPlan service = retrofit.create(RequestPlan.class);

		Call<List<unibo.progettotesi.json.planner.Response>> queryResponseCall = service.requestPlan(
				location.getLatitude() + "," + location.getLongitude(),
				locationS.getLatitude() + "," + locationS.getLongitude(),
				new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime()),
				unibo.progettotesi.utilities.Time.now() + "00", "CAR", "fastest", 1);

		queryResponseCall.enqueue(new Callback<List<unibo.progettotesi.json.planner.Response>>(){

			@Override
			public void onResponse(retrofit2.Response<List<unibo.progettotesi.json.planner.Response>> response) {
				try{
					if(response.body() != null /**/)
						setDistance(onTheGoActivity, response.body().get(0).getLeg().get(0).getLength(), next);
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

	private static void setDistance(OnTheGoActivity onTheGoActivity, double d, boolean next) {
		if(next)
			onTheGoActivity.setRoadd0((int) d);
		else
			onTheGoActivity.setRoadd1((int) d);
	}

	public interface RequestStops {
		@POST("getNextTrips")

		Call<Response> requestStops(@Body Request request);
	}

	private String coordinatesToAddress(double latitude, double longitude) {
		try{
			Geocoder geocoder = new Geocoder(onTheGoActivity, Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

			return addresses.get(0).getAddressLine(0);
		}catch (Exception e){
			e.printStackTrace();
			return "";
		}
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
