package collector.hotwheels.insight.imports;

import collector.hotwheels.insight.services.JIRAService;
import collector.hotwheels.insight.util.CacheProvider;
import collector.hotwheels.insight.util.I18FactoryFake;
import com.atlassian.adapter.jackson.ObjectMapper;
import com.google.common.base.Objects;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.ImportModuleConfiguration;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.insightfield.InsightFieldConfiguration;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.insightfield.checkbox.InsightFieldCheckboxConfiguration;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.insightfield.text.InsightFieldTextConfiguration;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static collector.hotwheels.insight.util.StringUtils.isNullOrEmpty;

public class ImportConfiguration implements ImportModuleConfiguration {

    private final Logger logger = LoggerFactory.getLogger(ImportConfiguration.class);

    private static final long serialVersionUID = -4473533132810886240L;
    private final String baseApiEndpoint = "https://hotwheels.fandom.com/";
    private int cacheExpiration = 5;
    private int requestLimit = -1;
    private String dataFolder;

    private InsightFieldConfiguration dataLimit;
    private InsightFieldConfiguration importImages;

    public ImportConfiguration() {
        CacheProvider.getInstance()
                .setExpiration(cacheExpiration);

        this.dataLimit = new InsightFieldTextConfiguration("dataLimit", I18FactoryFake.getInstance()
                .getProperty("rlabs.module.i18n.import.module.dataLimit"), I18FactoryFake.getInstance()
                .getProperty("rlabs.module.i18n.import.module.dataLimit.desc"));
        this.dataLimit.setMandatory(false);

        this.importImages = new InsightFieldCheckboxConfiguration("importImages", I18FactoryFake.getInstance()
                .getProperty("rlabs.module.i18n.import.module.importimages"), I18FactoryFake.getInstance()
                .getProperty("rlabs.module.i18n.import.module.importimages.desc"));
        this.importImages.setMandatory(false);
    }

    public String getBaseApiEndpoint() {
        return baseApiEndpoint;
    }

    public int getCacheExpiration() {
        return cacheExpiration;
    }

    public void setCacheExpiration(int cacheExpiration) {
        this.cacheExpiration = cacheExpiration;
    }

    public int getRequestLimit() {
        return requestLimit;
    }

    public void setRequestLimit(int requestLimit) {
        this.requestLimit = requestLimit;
    }

    public String getDataLimit() {
        return (String) this.dataLimit.getValue();
    }

    public void setDataLimit(String value) {
        this.dataLimit.setValue(value);
        this.requestLimit = -1;
        if (!isNullOrEmpty(value)) {
            try {
                this.requestLimit = Integer.parseInt(value);
            } catch (Exception ee) {
                logger.error("Error setting datalimit from: " + value, ee);
            }
        }
    }

    public boolean getImportImages() {
        return (boolean) (this.importImages.getValue() == null ? false : this.importImages.getValue());
    }

    public void setImportImages(boolean value) {
        this.importImages.setValue(value);
    }

    public String getDataFolder() {
        if (isNullOrEmpty(dataFolder)) {
            return JIRAService.JIRA_HOME_IMPORT_INSIGHT_HW_PATH;
        } else {
            return dataFolder;
        }
    }

    public void setDataFolder(String dataFolder) {
        this.dataFolder = dataFolder;
    }

    @Override
    public String toJSON() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    @Override
    @JsonIgnore
    public List<InsightFieldConfiguration> getFieldsConfiguration() {
        //return Collections.emptyList();
        return Arrays.asList(dataLimit, importImages);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dataLimit.getValue(), importImages.getValue());
    }

}
