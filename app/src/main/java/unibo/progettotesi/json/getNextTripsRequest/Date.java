package unibo.progettotesi.json.getNextTripsRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Date {

	@SerializedName("month")
	@Expose
	public Integer month;
	@SerializedName("year")
	@Expose
	public Integer year;
	@SerializedName("day")
	@Expose
	public Integer day;

	public Date(Integer month, Integer year, Integer day) {
		this.month = month;
		this.year = year;
		this.day = day;
	}
}