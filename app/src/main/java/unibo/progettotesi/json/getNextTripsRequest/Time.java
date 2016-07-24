package unibo.progettotesi.json.getNextTripsRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Time {

	@SerializedName("hour")
	@Expose
	public Integer hour;
	@SerializedName("minute")
	@Expose
	public Integer minute;
	@SerializedName("second")
	@Expose
	public Integer second;

	public Time(Integer hour, Integer minute, Integer second) {
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}
}
