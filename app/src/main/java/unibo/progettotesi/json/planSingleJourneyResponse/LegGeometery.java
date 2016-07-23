
package unibo.progettotesi.json.planSingleJourneyResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LegGeometery {

    @SerializedName("length")
    @Expose
    public Integer length;
    @SerializedName("levels")
    @Expose
    public Object levels;
    @SerializedName("points")
    @Expose
    public String points;

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Object getLevels() {
        return levels;
    }

    public void setLevels(Object levels) {
        this.levels = levels;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
