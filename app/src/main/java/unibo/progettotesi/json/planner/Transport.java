
package unibo.progettotesi.json.planner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transport {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("agencyId")
    @Expose
    private String agencyId;
    @SerializedName("routeId")
    @Expose
    private String routeId;
    @SerializedName("routeShortName")
    @Expose
    private String routeShortName;
    @SerializedName("tripId")
    @Expose
    private String tripId;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Transport() {
    }

    /**
     * 
     * @param tripId
     * @param routeShortName
     * @param routeId
     * @param type
     * @param agencyId
     */
    public Transport(String type, String agencyId, String routeId, String routeShortName, String tripId) {
        this.type = type;
        this.agencyId = agencyId;
        this.routeId = routeId;
        this.routeShortName = routeShortName;
        this.tripId = tripId;
    }

    /**
     * 
     * @return
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return
     *     The agencyId
     */
    public String getAgencyId() {
        return agencyId;
    }

    /**
     * 
     * @param agencyId
     *     The agencyId
     */
    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    /**
     * 
     * @return
     *     The routeId
     */
    public String getRouteId() {
        return routeId;
    }

    /**
     * 
     * @param routeId
     *     The routeId
     */
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    /**
     * 
     * @return
     *     The routeShortName
     */
    public String getRouteShortName() {
        return routeShortName;
    }

    /**
     * 
     * @param routeShortName
     *     The routeShortName
     */
    public void setRouteShortName(String routeShortName) {
        this.routeShortName = routeShortName;
    }

    /**
     * 
     * @return
     *     The tripId
     */
    public String getTripId() {
        return tripId;
    }

    /**
     * 
     * @param tripId
     *     The tripId
     */
    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

}
