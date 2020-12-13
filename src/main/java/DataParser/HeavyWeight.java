package DataParser;

import java.util.HashMap;
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
        "name",
        "dst_ip",
        "dst_port",
        "connectToOther"
})
public class HeavyWeight {

    @JsonProperty("ip")
    private String ip;
    @JsonProperty("port")
    private Integer port;
    @JsonProperty("name")
    private String name;
    @JsonProperty("dst_ip")
    String dst_ip;
    @JsonProperty("dst_port")
    String dst_port;
    @JsonProperty("connectToOther")
    Boolean connectToOther;
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

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("dst_ip")
    public String getDst_ip() {
        return dst_ip;
    }
    @JsonProperty("dst_ip")
    public void setDst_ip(String dst_ip) {
        this.dst_ip = dst_ip;
    }
    @JsonProperty("dst_port")
    public String getDst_port() {
        return dst_port;
    }
    @JsonProperty("dst_port")
    public void setDst_port(String dst_port) {
        this.dst_port = dst_port;
    }
    @JsonProperty("connectToOther")
    public Boolean getConnectToOther() {
        return connectToOther;
    }
    @JsonProperty("connectToOther")
    public void setConnectToOther(Boolean connectToOther) {
        this.connectToOther = connectToOther;
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