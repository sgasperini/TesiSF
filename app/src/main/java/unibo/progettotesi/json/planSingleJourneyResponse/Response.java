
package unibo.progettotesi.json.planSingleJourneyResponse;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("from")
    @Expose
    public From from;
    @SerializedName("to")
    @Expose
    public To to;
    @SerializedName("startime")
    @Expose
    public Integer startime;
    @SerializedName("endtime")
    @Expose
    public Integer endtime;
    @SerializedName("duration")
    @Expose
    public Integer duration;
    @SerializedName("walkingDuration")
    @Expose
    public Integer walkingDuration;
    @SerializedName("leg")
    @Expose
    public List<Leg> leg = new ArrayList<Leg>();
    @SerializedName("promoted")
    @Expose
    public Boolean promoted;

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

    public Integer getWalkingDuration() {
        return walkingDuration;
    }

    public void setWalkingDuration(Integer walkingDuration) {
        this.walkingDuration = walkingDuration;
    }

    public List<Leg> getLeg() {
        return leg;
    }

    public void setLeg(List<Leg> leg) {
        this.leg = leg;
    }

    public Boolean getPromoted() {
        return promoted;
    }

    public void setPromoted(Boolean promoted) {
        this.promoted = promoted;
    }
}
