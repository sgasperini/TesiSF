
package unibo.progettotesi.json.planner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class To {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("stopId")
    @Expose
    private StopId stopId;
    @SerializedName("stopCode")
    @Expose
    private String stopCode;
    @SerializedName("lon")
    @Expose
    private String lon;
    @SerializedName("lat")
    @Expose
    private String lat;

    /**
     * No args constructor for use in serialization
     * 
     */
    public To() {
    }

    /**
     * 
     * @param lon
     * @param stopId
     * @param name
     * @param stopCode
     * @param lat
     */
    public To(String name, StopId stopId, String stopCode, String lon, String lat) {
        this.name = name;
        this.stopId = stopId;
        this.stopCode = stopCode;
        this.lon = lon;
        this.lat = lat;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The stopId
     */
    public StopId getStopId() {
        return stopId;
    }

    /**
     * 
     * @param stopId
     *     The stopId
     */
    public void setStopId(StopId stopId) {
        this.stopId = stopId;
    }

    /**
     * 
     * @return
     *     The stopCode
     */
    public String getStopCode() {
        return stopCode;
    }

    /**
     * 
     * @param stopCode
     *     The stopCode
     */
    public void setStopCode(String stopCode) {
        this.stopCode = stopCode;
    }

    /**
     * 
     * @return
     *     The lon
     */
    public String getLon() {
        return lon;
    }

    /**
     * 
     * @param lon
     *     The lon
     */
    public void setLon(String lon) {
        this.lon = lon;
    }

    /**
     * 
     * @return
     *     The lat
     */
    public String getLat() {
        return lat;
    }

    /**
     * 
     * @param lat
     *     The lat
     */
    public void setLat(String lat) {
        this.lat = lat;
    }

}
