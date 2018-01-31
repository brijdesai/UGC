package in.dailyhunt.ugc.Utilities;

import in.dailyhunt.ugc.Recyclerview.PostCardView;

/**
 * Created by pinal on 29/1/18.
 */

public class Post {
    private String fileName;
    private String tags;

    public Post(String fileName, String tags) {
        this.fileName = fileName;
        this.tags = tags;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

}
