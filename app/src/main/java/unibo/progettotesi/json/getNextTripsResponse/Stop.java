
package unibo.progettotesi.json.getNextTripsResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stop {

    @SerializedName("stop_lon")
    @Expose
    public Double stopLon;
    @SerializedName("stop_id")
    @Expose
    public String stopId;
    @SerializedName("stop_lat")
    @Expose
    public Double stopLat;
    @SerializedName("departure_time")
    @Expose
    public String departureTime;
    @SerializedName("stop_name")
    @Expose
    public String stopName;

}
