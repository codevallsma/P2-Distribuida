package DataParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "type",
        "ip",
        "port",
        "name",
        "connectToOther",
        "nodes"
})
public class HeavyWeight extends Node{

    @JsonProperty("type")
    private String type;

    @JsonProperty("connectToOther")
    private Boolean connectToOther;

    @JsonProperty("nodes")
    private List<Node> nodes = null;

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