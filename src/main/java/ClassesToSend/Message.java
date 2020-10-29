package ClassesToSend;

import java.io.Serializable;

public class Message implements Serializable {
    private String tag;
    private int src;
    private int timestamp;

    public Message(String tag, int src, int timestamp) {
        this.tag = tag;
        this.src = src;
        this.timestamp = timestamp;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getSrc() {
        return src;
    }

    public void setSrc(int src) {
        this.src = src;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
