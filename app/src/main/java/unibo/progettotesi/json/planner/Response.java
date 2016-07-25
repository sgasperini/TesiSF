
package unibo.progettotesi.json.planner;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("from")
    @Expose
    private From from;
    @SerializedName("to")
    @Expose
    private To to;
    @SerializedName("startime")
    @Expose
    private Long startime;
    @SerializedName("endtime")
    @Expose
    private Long endtime;
    @SerializedName("duration")
    @Expose
    private Long duration;
    @SerializedName("walkingDuration")
    @Expose
    private Long walkingDuration;
    @SerializedName("leg")
    @Expose
    private List<Leg> leg = new ArrayList<Leg>();
    @SerializedName("promoted")
    @Expose
    private Boolean promoted;
    @SerializedName("customData")
    @Expose
    private CustomData customData;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Response() {
    }

    /**
     * 
     * @param endtime
     * @param to
     * @param leg
     * @param duration
     * @param startime
     * @param walkingDuration
     * @param promoted
     * @param customData
     * @param from
     */
    public Response(From from, To to, Long startime, Long endtime, Long duration, Long walkingDuration, List<Leg> leg, Boolean promoted, CustomData customData) {
        this.from = from;
        this.to = to;
        this.startime = startime;
        this.endtime = endtime;
        this.duration = duration;
        this.walkingDuration = walkingDuration;
        this.leg = leg;
        this.promoted = promoted;
        this.customData = customData;
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
     *     The walkingDuration
     */
    public Long getWalkingDuration() {
        return walkingDuration;
    }

    /**
     * 
     * @param walkingDuration
     *     The walkingDuration
     */
    public void setWalkingDuration(Long walkingDuration) {
        this.walkingDuration = walkingDuration;
    }

    /**
     * 
     * @return
     *     The leg
     */
    public List<Leg> getLeg() {
        return leg;
    }

    /**
     * 
     * @param leg
     *     The leg
     */
    public void setLeg(List<Leg> leg) {
        this.leg = leg;
    }

    /**
     * 
     * @return
     *     The promoted
     */
    public Boolean getPromoted() {
        return promoted;
    }

    /**
     * 
     * @param promoted
     *     The promoted
     */
    public void setPromoted(Boolean promoted) {
        this.promoted = promoted;
    }

    /**
     * 
     * @return
     *     The customData
     */
    public CustomData getCustomData() {
        return customData;
    }

    /**
     * 
     * @param customData
     *     The customData
     */
    public void setCustomData(CustomData customData) {
        this.customData = customData;
    }

}
