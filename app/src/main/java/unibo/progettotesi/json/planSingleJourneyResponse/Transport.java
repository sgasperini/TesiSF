
package unibo.progettotesi.json.planSingleJourneyResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transport {

    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("agencyId")
    @Expose
    public Object agencyId;
    @SerializedName("routeId")
    @Expose
    public Object routeId;
    @SerializedName("routeShortName")
    @Expose
    public Object routeShortName;
    @SerializedName("tripId")
    @Expose
    public Object tripId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Object agencyId) {
        this.agencyId = agencyId;
    }

    public Object getRouteId() {
        return routeId;
    }

    public void setRouteId(Object routeId) {
        this.routeId = routeId;
    }

    public Object getRouteShortName() {
        return routeShortName;
    }

    public void setRouteShortName(Object routeShortName) {
        this.routeShortName = routeShortName;
    }

    public Object getTripId() {
        return tripId;
    }

    public void setTripId(Object tripId) {
        this.tripId = tripId;
    }
}
