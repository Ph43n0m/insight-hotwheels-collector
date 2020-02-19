package collector.hotwheels.api.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

@JsonInclude (JsonInclude.Include.NON_NULL)
public final class Image {

    @JsonProperty ("imageserving")
    private String imageserving;
    @JsonProperty ("error")
    private String error;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty ("imageserving")
    public String getImageserving() {
        return imageserving;
    }

    @JsonProperty ("error")
    public void setError(String error) {
        this.error = error;
    }

    @JsonProperty ("error")
    public String getError() {
        return error;
    }

    @JsonProperty ("imageserving")
    public void setImageserving(String imageserving) {
        this.imageserving = imageserving;
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
