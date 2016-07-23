
package unibo.progettotesi.json.planSingleJourneyResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class From {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("stopId")
    @Expose
    public StopId stopId;
    @SerializedName("stopCode")
    @Expose
    public Object stopCode;
    @SerializedName("lon")
    @Expose
    public String lon;
    @SerializedName("lat")
    @Expose
    public String lat;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StopId getStopId() {
        return stopId;
    }

    public void setStopId(StopId stopId) {
        this.stopId = stopId;
    }

    public Object getStopCode() {
        return stopCode;
    }

    public void setStopCode(Object stopCode) {
        this.stopCode = stopCode;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
}
