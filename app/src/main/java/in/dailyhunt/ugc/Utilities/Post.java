package in.dailyhunt.ugc.Utilities;

/**
 * Created by pinal on 29/1/18.
 */

public class Post {
    String location;
    String tags;

    public Post(String location, String tags) {
        this.location = location;
        this.tags = tags;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
