package collector.hotwheels.insight.imports.manager;

import collector.hotwheels.api.client.WikiaClient;
import collector.hotwheels.insight.imports.ImportConfiguration;
import collector.hotwheels.insight.imports.manager.impl.CategoryService;
import collector.hotwheels.insight.imports.manager.impl.CollectionService;
import collector.hotwheels.insight.imports.manager.impl.ModelService;
import collector.hotwheels.insight.imports.manager.impl.ToyService;
import collector.hotwheels.insight.imports.model.ModuleSelector;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.DataLocator;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.ModuleOTSelector;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class AbstractService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractService.class);

    protected WikiaClient wikiaClient = null;
    protected ImportConfiguration configuration;
    protected List<ModuleOTSelector> enabledModuleOTSelectors;
    protected List<DataLocator> configuredDataLocators;

    private ITypeManager moduleTypeManager = null;

    public AbstractService(ImportConfiguration configuration,
            List<DataLocator> configuredDataLocators,
            List<ModuleOTSelector> enabledModuleOTSelectors) {
        this.configuration = configuration;
        this.enabledModuleOTSelectors = enabledModuleOTSelectors;
        this.configuredDataLocators = configuredDataLocators;
    }

    protected ITypeManager getDataTypeManager(ModuleOTSelector moduleOTSelector) {

        switch (ModuleSelector.getInstance(moduleOTSelector.getSelector())) {
            case CATEGORY:
                moduleTypeManager =
                        new CategoryService(configuration, configuredDataLocators, enabledModuleOTSelectors);
                break;
            case MODEL:
                moduleTypeManager = new ModelService(configuration, configuredDataLocators, enabledModuleOTSelectors);
                break;
            case TOY:
                moduleTypeManager = new ToyService(configuration, configuredDataLocators, enabledModuleOTSelectors);
                break;
            case COLLECTION:
                moduleTypeManager =
                        new CollectionService(configuration, configuredDataLocators, enabledModuleOTSelectors);
                break;
            default:
                break;
        }

        return moduleTypeManager;
    }

    public String getCacheKey() {
        return configuration.hashCode() + "_" + this.getClass()
                .getName();
    }

    protected boolean isModuleSelectorEnabled(ModuleSelector selector) {
        boolean ret = true;

        try {
            if (selector != null && enabledModuleOTSelectors != null) {
                ret = enabledModuleOTSelectors.stream()
                        .filter(o -> o.getSelector()
                                .equals(selector.getSelector()))
                        .findFirst()
                        .orElse(null) != null;
            }
        } catch (Exception ee) {
            logger.error("Error check available selector.");
        }
        return ret;
    }

    protected boolean isDataLocatorConfigured(DataLocator dataLocator) {
        boolean ret = true;

        try {
            if (dataLocator != null && configuredDataLocators != null) {
                ret = configuredDataLocators.stream()
                        .filter(o -> o.getLocator()
                                .equals(dataLocator.getLocator()))
                        .findFirst()
                        .orElse(null) != null;
            }
        } catch (Exception ee) {
            logger.error("Error check available dataLocator.");
        }
        return ret;
    }

    protected byte[] getAvatar(List<String[]> avatarList, List<String> objectName, String basepath) {

        if (objectName == null || objectName.size() != 1) {
            return null;
        }

        boolean avatarToSet = false;
        String filePath = null;
        for (String[] avatar : avatarList) {
            if (objectName.get(0)
                    .toLowerCase()
                    .contains(avatar[0])) {
                avatarToSet = true;
                filePath = avatar[1];
                break;
            }
        }

        if (avatarToSet) {
            try {
                InputStream in = StructureManager.class.getClassLoader()
                        .getResourceAsStream(basepath + filePath);
                if (in != null) {
                    return IOUtils.toByteArray(in);
                } else {
                    return null;
                }
            } catch (IOException e) {
                logger.warn("Could not get avatar for import", e);
            }
        }

        return null;
    }
}
