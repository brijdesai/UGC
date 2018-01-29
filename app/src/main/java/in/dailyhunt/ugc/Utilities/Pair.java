package in.dailyhunt.ugc.Utilities;

import java.io.File;

/**
 * Created by brij on 29/1/18.
 */

public class Pair {
    private String key;
    private String value;
    private File file;

    public Pair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Pair(String key, File file) {
        this.key = key;
        this.file = file;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }


}
