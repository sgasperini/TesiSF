
package unibo.progettotesi.json.createProfileRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Name {
    public Name(String string) {
        this.string = string;
    }

    @SerializedName("string")
    @Expose
    public String string;

}
