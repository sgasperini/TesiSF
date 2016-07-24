package unibo.progettotesi.json.getNextTripsRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Request {

	@SerializedName("date")
	@Expose
	public Date date;
	@SerializedName("direction_id")
	@Expose
	public Integer directionId;
	@SerializedName("route_id")
	@Expose
	public String routeId;
	@SerializedName("start_stop_id")
	@Expose
	public String startStopId;
	@SerializedName("max_trips")
	@Expose
	public Integer maxTrips;
	@SerializedName("end_stop_id")
	@Expose
	public String endStopId;
	@SerializedName("time")
	@Expose
	public Time time;

	public Request(Date date, Integer directionId, String routeId, String startStopId, Integer maxTrips, String endStopId, Time time) {
		this.date = date;
		this.directionId = directionId;
		this.routeId = routeId;
		this.startStopId = startStopId;
		this.maxTrips = maxTrips;
		this.endStopId = endStopId;
		this.time = time;
	}
}