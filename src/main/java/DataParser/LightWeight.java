package DataParser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;


public class LightWeight extends Node{
    @JsonProperty("nodeId")
    private Integer nodeId;

    @JsonProperty("connectedTo")
    private List<Integer> connectedTo = new ArrayList<>();

    @JsonCreator
    LightWeight( @JsonProperty("name") String name, @JsonProperty("ip") String ip, @JsonProperty("port") Integer port,  @JsonProperty("nodeId")Integer nodeId,@JsonProperty("connectedTo") List<Integer> connectedTo )
    {
        super(name,ip, port);
        this.nodeId = nodeId;
        this.connectedTo = connectedTo;
    }

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
