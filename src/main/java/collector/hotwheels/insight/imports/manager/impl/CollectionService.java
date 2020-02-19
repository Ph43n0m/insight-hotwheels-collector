package collector.hotwheels.insight.imports.manager.impl;

import collector.hotwheels.api.model.PageData;
import collector.hotwheels.insight.imports.ImportConfiguration;
import collector.hotwheels.insight.imports.ModuleProperties;
import collector.hotwheels.insight.imports.manager.ClientProvider;
import collector.hotwheels.insight.imports.manager.IData;
import collector.hotwheels.insight.imports.manager.ITypeManager;
import collector.hotwheels.insight.imports.model.locators.CollectionModelsLocator;
import collector.hotwheels.insight.imports.model.locators.ModelCategoriesLocator;
import collector.hotwheels.insight.imports.model.locators.ModelDescriptionLocator;
import collector.hotwheels.insight.imports.model.locators.base.JavaCodeDataLocator;
import collector.hotwheels.insight.imports.model.locators.base.ModuleDataLocator;
import collector.hotwheels.insight.imports.model.locators.base.ReferencedDataLocator;
import collector.hotwheels.insight.imports.model.locators.base.StandardDataLocator;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.DataLocator;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.ImportDataValue;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.ImportDataValues;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.ModuleOTSelector;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionService extends AbstractBaseService implements ITypeManager, IData {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(CollectionService.class);

    public final static ModuleDataLocator description = new ModelDescriptionLocator(ModuleProperties.DESCRIPTION);

    public final static ReferencedDataLocator<PageData, Object> models =
            new CollectionModelsLocator(ModuleProperties.MODELS);
    public final static ReferencedDataLocator<PageData, Object> categories =
            new ModelCategoriesLocator(ModuleProperties.CATEGORIES);

    public CollectionService(ImportConfiguration configuration,
            List<DataLocator> configuredDataLocators,
            List<ModuleOTSelector> enabledModuleOTSelectors) {
        super(configuration, configuredDataLocators, enabledModuleOTSelectors);
        dataLocators.add(categories);
        dataLocators.add(description);
        dataLocators.add(models);
    }

    @Override
    public List<DataLocator> getDataLocators() {
        return new ArrayList<>(dataLocators);
    }

    @SuppressWarnings ("unchecked")
    @Override
    public ImportDataValues getDataHolder() {

        List<ImportDataValue> dataMap = new ArrayList<>();

        for (PageData obj : getData()) {
            Map<DataLocator, List<String>> keyValueMap = new HashMap<>();
            for (ModuleDataLocator moduleDataLocator : dataLocators) {
                try {
                    if (!isDataLocatorConfigured(moduleDataLocator)) {
                        continue;
                    }
                    switch (moduleDataLocator.getLocatorType()) {
                        case JAVA_METHOD_VALUE:
                            keyValueMap.put(moduleDataLocator,
                                    ((StandardDataLocator) moduleDataLocator).getMethodValue(obj));
                            break;
                        case JAVA_CODE_VALUE:
                            keyValueMap.put(moduleDataLocator,
                                    ((JavaCodeDataLocator) moduleDataLocator).getCodeLocatorValue(obj));
                            break;
                        case REFERENCED_VALUE:
                            keyValueMap.put(moduleDataLocator,
                                    ((ReferencedDataLocator<PageData, Object>) moduleDataLocator).getReferencedLocatorValue(
                                            obj, null));
                            break;
                        default:
                            break;
                    }
                } catch (Exception ee) {
                    logger.warn("Could not add value from locator: " + moduleDataLocator.getLocator(), ee);
                }
            }
            byte[] avatarBytes = getAvatarBytes(obj);
            dataMap.add(new ImportDataValue(keyValueMap, avatarBytes));
        }

        return ImportDataValues.create(dataMap);
    }

    @Override
    public List<PageData> getData() {
        logger.debug("Fetching Collection Data.");

        Set<PageData> ret = new HashSet<>();
        try {
            wikiaClient = ClientProvider.INSTANCE.getClient(configuration);
            if (wikiaClient != null) {
                ret = wikiaClient.collections()
                        .list()
                        .execute();
            }
        } catch (Exception ex) {
            logger.error("Error fetch Collection data", ex);
        }

        return ret.isEmpty() ? null : new ArrayList<>(ret);
    }

}
