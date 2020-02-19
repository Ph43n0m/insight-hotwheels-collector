package collector.hotwheels.insight.imports.manager.impl;

import collector.hotwheels.api.model.Category;
import collector.hotwheels.insight.imports.ImportConfiguration;
import collector.hotwheels.insight.imports.ModuleProperties;
import collector.hotwheels.insight.imports.manager.ClientProvider;
import collector.hotwheels.insight.imports.manager.IData;
import collector.hotwheels.insight.imports.manager.ITypeManager;
import collector.hotwheels.insight.imports.model.locators.CategoryNameLocator;
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

public class CategoryService extends AbstractBaseService implements ITypeManager, IData {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(CategoryService.class);

    public final static ModuleDataLocator name = new CategoryNameLocator(ModuleProperties.NAME);

    public CategoryService(ImportConfiguration configuration,
            List<DataLocator> configuredDataLocators,
            List<ModuleOTSelector> enabledModuleOTSelectors) {
        super(configuration, configuredDataLocators, enabledModuleOTSelectors);
        dataLocators.remove(name);
        dataLocators.add(name);
    }

    @Override
    public List<DataLocator> getDataLocators() {
        return new ArrayList<>(dataLocators);
    }

    @SuppressWarnings ("unchecked")
    @Override
    public ImportDataValues getDataHolder() {

        List<ImportDataValue> dataMap = new ArrayList<>();

        for (Category obj : getData()) {
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
                                    ((ReferencedDataLocator<Category, Object>) moduleDataLocator).getReferencedLocatorValue(
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
    public List<Category> getData() {
        logger.debug("Fetching Category Data.");

        Set<Category> ret = new HashSet<>();
        try {
            wikiaClient = ClientProvider.INSTANCE.getClient(configuration);
            if (wikiaClient != null) {
                ret = wikiaClient.categories()
                        .list()
                        .execute();
            }
        } catch (Exception ex) {
            logger.error("Error fetch Category data", ex);
        }

        return ret.isEmpty() ? null : new ArrayList<>(ret);
    }

}
