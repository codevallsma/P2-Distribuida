package DataParser;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "ip",
        "port",
        "nodeId",
        "name",
        "connectedTo"
})
public class LightWeight extends Node{
    @JsonProperty("nodeId")
    private Integer nodeId;

    @JsonProperty("connectedTo")
    private List<Integer> connectedTo = null;

    @JsonProperty("nodeId")
    public Integer getNodeId() {
        return nodeId;
    }

    @JsonProperty("nodeId")
    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    @JsonProperty("connectedTo")
    public List<Integer> getConnectedTo() {
        return connectedTo;
    }

    @JsonProperty("connectedTo")
    public void setConnectedTo(List<Integer> connectedTo) {
        this.connectedTo = connectedTo;
    }
}
