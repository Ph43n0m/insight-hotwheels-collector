package collector.hotwheels.insight.imports.manager;

import collector.hotwheels.insight.imports.ImportConfiguration;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.DataLocator;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.ImportDataValues;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.ModuleOTSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ContentManager extends AbstractService {

    private final Logger logger = LoggerFactory.getLogger(ContentManager.class);

    public ContentManager(ImportConfiguration configuration,
            List<DataLocator> configuredDataLocators,
            List<ModuleOTSelector> enabledModuleOTSelectors) {
        super(configuration, configuredDataLocators, enabledModuleOTSelectors);
    }

    public ImportDataValues getDataEntries(ModuleOTSelector moduleOTSelector) {

        ITypeManager typeManager = getDataTypeManager(moduleOTSelector);
        return typeManager != null ? typeManager.getDataHolder() : ImportDataValues.create(new ArrayList<>());
    }

    public boolean validate(ModuleOTSelector moduleOTSelector) {
        ITypeManager typeManager = getDataTypeManager(moduleOTSelector);
        return typeManager != null;
    }
}