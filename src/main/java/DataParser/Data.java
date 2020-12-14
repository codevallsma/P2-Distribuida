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
        "HeavyWeights"
})
public class Data {

    @JsonProperty("HeavyWeights")
    private List<HeavyWeight> heavyWeights = null;

    @JsonProperty("HeavyWeights")
    public List<HeavyWeight> getHeavyWeights() {
        return heavyWeights;
    }

    @JsonProperty("HeavyWeights")
    public void setHeavyWeights(List<HeavyWeight> heavyWeights) {
        this.heavyWeights = heavyWeights;
    }
}
