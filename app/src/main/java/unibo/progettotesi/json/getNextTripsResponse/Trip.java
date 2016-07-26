
package unibo.progettotesi.json.getNextTripsResponse;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Trip {

    @SerializedName("trip_id")
    @Expose
    public String tripId;
    @SerializedName("service_id")
    @Expose
    public String serviceId;
    @SerializedName("stops")
    @Expose
    public List<Stop> stops = new ArrayList<Stop>();

}
