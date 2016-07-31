
package unibo.progettotesi.json.busETA;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("result")
    @Expose
    public Result result;

}
