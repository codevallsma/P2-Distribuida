package ClassesToSend;

import java.io.Serializable;

public class Message<T> implements Serializable {
    private String tag;
    private int src;
    private int timestamp;
    private T content;

    public Message(String tag, int src, int timestamp, T content) {
        this.tag = tag;
        this.src = src;
        this.timestamp = timestamp;
        this.content = content;
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

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
