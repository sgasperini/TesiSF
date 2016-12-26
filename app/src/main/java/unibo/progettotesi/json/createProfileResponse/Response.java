
package unibo.progettotesi.json.createProfileResponse;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("ownerId")
    @Expose
    public String ownerId;
    @SerializedName("creationDate")
    @Expose
    public Integer creationDate;
    @SerializedName("lastUpdate")
    @Expose
    public Integer lastUpdate;
    @SerializedName("profileId")
    @Expose
    public String profileId;
    @SerializedName("content")
    @Expose
    public Content content;
    @SerializedName("userId")
    @Expose
    public String userId;
    @SerializedName("name")
    @Expose
    public Name name;
    @SerializedName("description")
    @Expose
    public Description description;
    @SerializedName("tags")
    @Expose
    public List<String> tags = new ArrayList<String>();

}
