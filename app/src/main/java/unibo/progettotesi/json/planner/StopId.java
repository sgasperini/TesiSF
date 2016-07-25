
package unibo.progettotesi.json.planner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StopId {

    @SerializedName("agencyId")
    @Expose
    private String agencyId;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("extra")
    @Expose
    private Object extra;

    /**
     * No args constructor for use in serialization
     * 
     */
    public StopId() {
    }

    /**
     * 
     * @param id
     * @param extra
     * @param agencyId
     */
    public StopId(String agencyId, String id, Object extra) {
        this.agencyId = agencyId;
        this.id = id;
        this.extra = extra;
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
     *     The id
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(String id) {
        this.id = id;
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

}
