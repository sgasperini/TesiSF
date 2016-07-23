
package unibo.progettotesi.json.planSingleJourneyResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StopId {

    @SerializedName("agencyId")
    @Expose
    public String agencyId;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("extra")
    @Expose
    public Object extra;

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }
}
