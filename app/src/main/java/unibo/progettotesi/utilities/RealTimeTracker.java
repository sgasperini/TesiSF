package unibo.progettotesi.utilities;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
import unibo.progettotesi.activities.OnTheGoActivity;
import unibo.progettotesi.json.getNextTripsRequest.Date;
import unibo.progettotesi.json.getNextTripsRequest.Request;
import unibo.progettotesi.json.getNextTripsRequest.Time;
import unibo.progettotesi.json.getNextTripsResponse.Response;
import unibo.progettotesi.model.Leg;
import unibo.progettotesi.model.Line;
import unibo.progettotesi.model.Location;
import unibo.progettotesi.model.Stop;

public class RealTimeTracker {
	private OnTheGoActivity onTheGoActivity;
	private Leg currentLeg;

	public static void setDistanceTo(TextView textView, Stop stop){
		textView.setText(500 + "");
	}

	public static void setBusTime(TextView textView, Stop stop, Line line, unibo.progettotesi.utilities.Time time){
		textView.setText(new unibo.progettotesi.utilities.Time(10, 30).toString());
	}

	public void getStopsFromWeb(OnTheGoActivity onTheGoActivity, final Leg currentLeg){
		this.onTheGoActivity = onTheGoActivity;
		this.currentLeg = currentLeg;

		Retrofit retrofit = new Retrofit.Builder()      //create the retrofit builder
				.baseUrl(Constants.GET_NEXT_TRIPS_BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())	//parse Gson string
				.build();

		RequestStops service = retrofit.create(RequestStops.class);

		Call<Response> queryResponseCall = service.requestStops(new Request(new Date(7, 2016, 22), 0, "19", "6012", 1, "1041", new unibo.progettotesi.json.getNextTripsRequest.Time(4,30,0)));

		queryResponseCall.enqueue(new Callback<Response>(){

			@Override
			public void onResponse(retrofit2.Response<Response> response) {
				try{
					if(response.body() != null /**/)
						stopToInterStopConverter(response.body().trips.stops);
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

	public interface RequestStops {
		@POST("getNextTrips")
		/*Call<Response> requestStops(@Query("date") Date date,
									 @Query("direction_id") int directionID,
									 @Query("route_id") String routeID,
									 @Query("start_stop_id") String startStopID,
									 @Query("max_trips") int maxTrips,
									 @Query("end_stop_id") String endStopID,
									 @Query("time") Time time);*/

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

}
