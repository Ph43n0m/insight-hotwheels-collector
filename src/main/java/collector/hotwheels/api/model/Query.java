package collector.hotwheels.api.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude (JsonInclude.Include.NON_NULL)
public final class Query {

    @JsonProperty ("pages")
    private Pages pages;
    @JsonProperty ("allcategories")
    private List<AllCategories> allcategories = null;
    @JsonProperty ("categorymembers")
    private List<CategoryMembers> categorymembers = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty ("pages")
    public Pages getPages() {
        return pages;
    }

    @JsonProperty ("pages")
    public void setPages(Pages pages) {
        this.pages = pages;
    }

    @JsonProperty ("allcategories")
    public List<AllCategories> getAllcategories() {
        return allcategories;
    }

    @JsonProperty ("allcategories")
    public void setAllcategories(List<AllCategories> allcategories) {
        this.allcategories = allcategories;
    }

    @JsonProperty ("categorymembers")
    public List<CategoryMembers> getCategorymembers() {
        return categorymembers;
    }

    @JsonProperty ("categorymembers")
    public void setCategorymembers(List<CategoryMembers> categorymembers) {
        this.categorymembers = categorymembers;
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
