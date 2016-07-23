
package unibo.progettotesi.json.planSingleJourneyResponse;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Leg {

    @SerializedName("legId")
    @Expose
    public String legId;
    @SerializedName("startime")
    @Expose
    public Integer startime;
    @SerializedName("endtime")
    @Expose
    public Integer endtime;
    @SerializedName("duration")
    @Expose
    public Integer duration;
    @SerializedName("from")
    @Expose
    public From from;
    @SerializedName("to")
    @Expose
    public To to;
    @SerializedName("transport")
    @Expose
    public Transport transport;
    @SerializedName("legGeometery")
    @Expose
    public LegGeometery legGeometery;
    @SerializedName("alertStrikeList")
    @Expose
    public List<Object> alertStrikeList = new ArrayList<Object>();
    @SerializedName("alertDelayList")
    @Expose
    public List<Object> alertDelayList = new ArrayList<Object>();
    @SerializedName("alertParkingList")
    @Expose
    public List<Object> alertParkingList = new ArrayList<Object>();
    @SerializedName("alertRoadList")
    @Expose
    public List<Object> alertRoadList = new ArrayList<Object>();
    @SerializedName("alertAccidentList")
    @Expose
    public List<Object> alertAccidentList = new ArrayList<Object>();
    @SerializedName("extra")
    @Expose
    public Object extra;
    @SerializedName("length")
    @Expose
    public Double length;

    public String getLegId() {
        return legId;
    }

    public void setLegId(String legId) {
        this.legId = legId;
    }

    public Integer getStartime() {
        return startime;
    }

    public void setStartime(Integer startime) {
        this.startime = startime;
    }

    public Integer getEndtime() {
        return endtime;
    }

    public void setEndtime(Integer endtime) {
        this.endtime = endtime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public From getFrom() {
        return from;
    }

    public void setFrom(From from) {
        this.from = from;
    }

    public To getTo() {
        return to;
    }

    public void setTo(To to) {
        this.to = to;
    }

    public Transport getTransport() {
        return transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public LegGeometery getLegGeometery() {
        return legGeometery;
    }

    public void setLegGeometery(LegGeometery legGeometery) {
        this.legGeometery = legGeometery;
    }

    public List<Object> getAlertStrikeList() {
        return alertStrikeList;
    }

    public void setAlertStrikeList(List<Object> alertStrikeList) {
        this.alertStrikeList = alertStrikeList;
    }

    public List<Object> getAlertDelayList() {
        return alertDelayList;
    }

    public void setAlertDelayList(List<Object> alertDelayList) {
        this.alertDelayList = alertDelayList;
    }

    public List<Object> getAlertParkingList() {
        return alertParkingList;
    }

    public void setAlertParkingList(List<Object> alertParkingList) {
        this.alertParkingList = alertParkingList;
    }

    public List<Object> getAlertRoadList() {
        return alertRoadList;
    }

    public void setAlertRoadList(List<Object> alertRoadList) {
        this.alertRoadList = alertRoadList;
    }

    public List<Object> getAlertAccidentList() {
        return alertAccidentList;
    }

    public void setAlertAccidentList(List<Object> alertAccidentList) {
        this.alertAccidentList = alertAccidentList;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }
}
