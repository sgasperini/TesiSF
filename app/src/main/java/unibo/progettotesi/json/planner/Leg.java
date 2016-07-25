
package unibo.progettotesi.json.planner;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Leg {

    @SerializedName("legId")
    @Expose
    private String legId;
    @SerializedName("startime")
    @Expose
    private Long startime;
    @SerializedName("endtime")
    @Expose
    private Long endtime;
    @SerializedName("duration")
    @Expose
    private Long duration;
    @SerializedName("from")
    @Expose
    private From from;
    @SerializedName("to")
    @Expose
    private To to;
    @SerializedName("transport")
    @Expose
    private Transport transport;
    @SerializedName("legGeometery")
    @Expose
    private LegGeometery legGeometery;
    @SerializedName("alertStrikeList")
    @Expose
    private List<Object> alertStrikeList = new ArrayList<Object>();
    @SerializedName("alertDelayList")
    @Expose
    private List<Object> alertDelayList = new ArrayList<Object>();
    @SerializedName("alertParkingList")
    @Expose
    private List<Object> alertParkingList = new ArrayList<Object>();
    @SerializedName("alertRoadList")
    @Expose
    private List<Object> alertRoadList = new ArrayList<Object>();
    @SerializedName("alertAccidentList")
    @Expose
    private List<Object> alertAccidentList = new ArrayList<Object>();
    @SerializedName("extra")
    @Expose
    private Object extra;
    @SerializedName("length")
    @Expose
    private Double length;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Leg() {
    }

    /**
     * 
     * @param endtime
     * @param to
     * @param alertParkingList
     * @param alertRoadList
     * @param alertAccidentList
     * @param startime
     * @param extra
     * @param alertDelayList
     * @param from
     * @param legGeometery
     * @param duration
     * @param transport
     * @param length
     * @param alertStrikeList
     * @param legId
     */
    public Leg(String legId, Long startime, Long endtime, Long duration, From from, To to, Transport transport, LegGeometery legGeometery, List<Object> alertStrikeList, List<Object> alertDelayList, List<Object> alertParkingList, List<Object> alertRoadList, List<Object> alertAccidentList, Object extra, Double length) {
        this.legId = legId;
        this.startime = startime;
        this.endtime = endtime;
        this.duration = duration;
        this.from = from;
        this.to = to;
        this.transport = transport;
        this.legGeometery = legGeometery;
        this.alertStrikeList = alertStrikeList;
        this.alertDelayList = alertDelayList;
        this.alertParkingList = alertParkingList;
        this.alertRoadList = alertRoadList;
        this.alertAccidentList = alertAccidentList;
        this.extra = extra;
        this.length = length;
    }

    /**
     * 
     * @return
     *     The legId
     */
    public String getLegId() {
        return legId;
    }

    /**
     * 
     * @param legId
     *     The legId
     */
    public void setLegId(String legId) {
        this.legId = legId;
    }

    /**
     * 
     * @return
     *     The startime
     */
    public Long getStartime() {
        return startime;
    }

    /**
     * 
     * @param startime
     *     The startime
     */
    public void setStartime(Long startime) {
        this.startime = startime;
    }

    /**
     * 
     * @return
     *     The endtime
     */
    public Long getEndtime() {
        return endtime;
    }

    /**
     * 
     * @param endtime
     *     The endtime
     */
    public void setEndtime(Long endtime) {
        this.endtime = endtime;
    }

    /**
     * 
     * @return
     *     The duration
     */
    public Long getDuration() {
        return duration;
    }

    /**
     * 
     * @param duration
     *     The duration
     */
    public void setDuration(Long duration) {
        this.duration = duration;
    }

    /**
     * 
     * @return
     *     The from
     */
    public From getFrom() {
        return from;
    }

    /**
     * 
     * @param from
     *     The from
     */
    public void setFrom(From from) {
        this.from = from;
    }

    /**
     * 
     * @return
     *     The to
     */
    public To getTo() {
        return to;
    }

    /**
     * 
     * @param to
     *     The to
     */
    public void setTo(To to) {
        this.to = to;
    }

    /**
     * 
     * @return
     *     The transport
     */
    public Transport getTransport() {
        return transport;
    }

    /**
     * 
     * @param transport
     *     The transport
     */
    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    /**
     * 
     * @return
     *     The legGeometery
     */
    public LegGeometery getLegGeometery() {
        return legGeometery;
    }

    /**
     * 
     * @param legGeometery
     *     The legGeometery
     */
    public void setLegGeometery(LegGeometery legGeometery) {
        this.legGeometery = legGeometery;
    }

    /**
     * 
     * @return
     *     The alertStrikeList
     */
    public List<Object> getAlertStrikeList() {
        return alertStrikeList;
    }

    /**
     * 
     * @param alertStrikeList
     *     The alertStrikeList
     */
    public void setAlertStrikeList(List<Object> alertStrikeList) {
        this.alertStrikeList = alertStrikeList;
    }

    /**
     * 
     * @return
     *     The alertDelayList
     */
    public List<Object> getAlertDelayList() {
        return alertDelayList;
    }

    /**
     * 
     * @param alertDelayList
     *     The alertDelayList
     */
    public void setAlertDelayList(List<Object> alertDelayList) {
        this.alertDelayList = alertDelayList;
    }

    /**
     * 
     * @return
     *     The alertParkingList
     */
    public List<Object> getAlertParkingList() {
        return alertParkingList;
    }

    /**
     * 
     * @param alertParkingList
     *     The alertParkingList
     */
    public void setAlertParkingList(List<Object> alertParkingList) {
        this.alertParkingList = alertParkingList;
    }

    /**
     * 
     * @return
     *     The alertRoadList
     */
    public List<Object> getAlertRoadList() {
        return alertRoadList;
    }

    /**
     * 
     * @param alertRoadList
     *     The alertRoadList
     */
    public void setAlertRoadList(List<Object> alertRoadList) {
        this.alertRoadList = alertRoadList;
    }

    /**
     * 
     * @return
     *     The alertAccidentList
     */
    public List<Object> getAlertAccidentList() {
        return alertAccidentList;
    }

    /**
     * 
     * @param alertAccidentList
     *     The alertAccidentList
     */
    public void setAlertAccidentList(List<Object> alertAccidentList) {
        this.alertAccidentList = alertAccidentList;
    }

    /**
     * 
     * @return
     *     The extra
     */
    public Object getExtra() {
        return extra;
    }

    /**
     * 
     * @param extra
     *     The extra
     */
    public void setExtra(Object extra) {
        this.extra = extra;
    }

    /**
     * 
     * @return
     *     The length
     */
    public Double getLength() {
        return length;
    }

    /**
     * 
     * @param length
     *     The length
     */
    public void setLength(Double length) {
        this.length = length;
    }

}
