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
        "heavyWeight",
        "nodes"
})

public class Data {
    @JsonProperty("heavyWeight")
    private HeavyWeight heavyWeight;
    @JsonProperty("nodes")
    private List<Node> nodes = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    @JsonProperty("heavyWeight")
    public HeavyWeight getHeavyWeight() {
        return heavyWeight;
    }

    @JsonProperty("heavyWeight")
    public void setHeavyWeight(HeavyWeight heavyWeight) {
        this.heavyWeight = heavyWeight;
    }
    @JsonProperty("nodes")
    public List<Node> getNodes() {
        return nodes;
    }

    @JsonProperty("nodes")
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
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
