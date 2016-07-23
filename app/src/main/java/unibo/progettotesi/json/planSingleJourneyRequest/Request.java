
package unibo.progettotesi.json.planSingleJourneyRequest;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Request {

    @SerializedName("to")
    @Expose
    private To to;
    @SerializedName("routeType")
    @Expose
    private String routeType;
    @SerializedName("resultsNumber")
    @Expose
    private Integer resultsNumber;
    @SerializedName("departureTime")
    @Expose
    private String departureTime;
    @SerializedName("from")
    @Expose
    private From from;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("transportTypes")
    @Expose
    private List<String> transportTypes = new ArrayList<String>();

    public Request(To to, String routeType, Integer resultsNumber, String departureTime, From from, String date, List<String> transportTypes) {
        this.to = to;
        this.routeType = routeType;
        this.resultsNumber = resultsNumber;
        this.departureTime = departureTime;
        this.from = from;
        this.date = date;
        this.transportTypes = transportTypes;
    }

    public To getTo() {
        return to;
    }

    public void setTo(To to) {
        this.to = to;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public Integer getResultsNumber() {
        return resultsNumber;
    }

    public void setResultsNumber(Integer resultsNumber) {
        this.resultsNumber = resultsNumber;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public From getFrom() {
        return from;
    }

    public void setFrom(From from) {
        this.from = from;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getTransportTypes() {
        return transportTypes;
    }

    public void setTransportTypes(List<String> transportTypes) {
        this.transportTypes = transportTypes;
    }
}
