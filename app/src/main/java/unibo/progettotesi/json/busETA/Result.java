
package unibo.progettotesi.json.busETA;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("route")
    @Expose
    public String route;
    @SerializedName("eta")
    @Expose
    public String eta;
    @SerializedName("serial")
    @Expose
    public String serial;

}
