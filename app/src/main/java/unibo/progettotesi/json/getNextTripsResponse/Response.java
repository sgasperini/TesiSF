
package unibo.progettotesi.json.getNextTripsResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("trips")
    @Expose
    public Trips trips;

}
