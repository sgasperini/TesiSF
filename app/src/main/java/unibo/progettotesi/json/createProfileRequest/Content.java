
package unibo.progettotesi.json.createProfileRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {

    @SerializedName("string")
    @Expose
    public String string;


    public Content(String string) {
        this.string = string;
    }
}
