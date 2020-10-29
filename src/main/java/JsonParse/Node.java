package JsonParse;

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
        "ip",
        "port",
        "nodeId",
        "connectedTo"
})
public class Node {

    @JsonProperty("ip")
    private String ip;
    @JsonProperty("port")
    private Integer port;
    @JsonProperty("nodeId")
    private Integer nodeId;
    @JsonProperty("connectedTo")
    private List<Integer> connectedTo = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("ip")
    public String getIp() {
        return ip;
    }

    @JsonProperty("ip")
    public void setIp(String ip) {
        this.ip = ip;
    }

    @JsonProperty("port")
    public Integer getPort() {
        return port;
    }

    @JsonProperty("port")
    public void setPort(Integer port) {
        this.port = port;
    }

    @JsonProperty("connectedTo")
    public List<Integer> getConnectedTo() {
        return connectedTo;
    }

    @JsonProperty("connectedTo")
    public void setConnectedTo(List<Integer> connectedTo) {
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}