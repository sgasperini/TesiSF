
package unibo.progettotesi.json.planner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LegGeometery {

    @SerializedName("length")
    @Expose
    private Integer length;
    @SerializedName("levels")
    @Expose
    private Object levels;
    @SerializedName("points")
    @Expose
    private String points;

    /**
     * No args constructor for use in serialization
     * 
     */
    public LegGeometery() {
    }

    /**
     * 
     * @param levels
     * @param length
     * @param points
     */
    public LegGeometery(Integer length, Object levels, String points) {
        this.length = length;
        this.levels = levels;
        this.points = points;
    }

    /**
     * 
     * @return
     *     The length
     */
    public Integer getLength() {
        return length;
    }

    /**
     * 
     * @param length
     *     The length
     */
    public void setLength(Integer length) {
        this.length = length;
    }

    /**
     * 
     * @return
     *     The levels
     */
    public Object getLevels() {
        return levels;
    }

    /**
     * 
     * @param levels
     *     The levels
     */
    public void setLevels(Object levels) {
        this.levels = levels;
    }

    /**
     * 
     * @return
     *     The points
     */
    public String getPoints() {
        return points;
    }

    /**
     * 
     * @param points
     *     The points
     */
    public void setPoints(String points) {
        this.points = points;
    }

}
