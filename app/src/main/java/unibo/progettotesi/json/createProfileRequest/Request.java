
package unibo.progettotesi.json.createProfileRequest;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Request {

    @SerializedName("ownerId")
    @Expose
    public String ownerId;
    @SerializedName("creationDate")
    @Expose
    public String creationDate;
    @SerializedName("lastUpdate")
    @Expose
    public String lastUpdate;
    @SerializedName("profileId")
    @Expose
    public String profileId;
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
    @SerializedName("content")
    @Expose
    public Content content;

    public Request(String ownerId, String creationDate, String lastUpdate, String profileId, String userId,
                   Name name, Description description, List<String> tags, Content content) {
        this.ownerId = ownerId;
        this.creationDate = creationDate;
        this.lastUpdate = lastUpdate;
        this.profileId = profileId;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.content = content;
    }
}
