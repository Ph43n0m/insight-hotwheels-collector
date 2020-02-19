package collector.hotwheels.api.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude (JsonInclude.Include.NON_NULL)
@JsonPropertyOrder ({"query", "query-continue"})
public final class QueryResponse {

    @JsonProperty ("query")
    private Query query;
    @JsonProperty ("query-continue")
    private QueryContinue queryContinue;
    @JsonProperty ("image")
    private Image image;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty ("query")
    public Query getQuery() {
        return query;
    }

    @JsonProperty ("query")
    public void setQuery(Query query) {
        this.query = query;
    }

    @JsonProperty ("query-continue")
    public QueryContinue getQueryContinue() {
        return queryContinue;
    }

    @JsonProperty ("query-continue")
    public void setQueryContinue(QueryContinue queryContinue) {
        this.queryContinue = queryContinue;
    }

    @JsonProperty ("image")
    public Image getImage() {
        return image;
    }

    @JsonProperty ("image")
    public void setQImage(Image image) {
        this.image = image;
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
