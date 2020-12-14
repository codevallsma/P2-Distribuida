package DataParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;

public class HeavyWeight extends Node{

    @JsonProperty("type")
    private String type;

    @JsonProperty("connectToOther")
    private Boolean connectToOther;

    @JsonProperty("nodes")
    private List<Node> nodes = null;

    @JsonCreator
    HeavyWeight( @JsonProperty("name") String name, @JsonProperty("ip") String ip, @JsonProperty("port") Integer port,  @JsonProperty("type")String type, @JsonProperty("connectToOther") Boolean connectToOther, @JsonProperty("connectedTo") List<Node> nodes )
    {
        super(name,ip, port);
        this.type = type;
        this.nodes = nodes;
        this.connectToOther = connectToOther;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("connectToOther")
    public Boolean getConnectToOther() {
        return connectToOther;
    }

    @JsonProperty("connectToOther")
    public void setConnectToOther(Boolean connectToOther) {
        this.connectToOther = connectToOther;
    }

    @JsonProperty("nodes")
    public List<Node> getNodes() {
        return nodes;
    }

    @JsonProperty("nodes")
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}