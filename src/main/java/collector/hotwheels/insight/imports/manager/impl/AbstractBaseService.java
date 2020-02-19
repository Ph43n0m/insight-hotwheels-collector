package collector.hotwheels.insight.imports.manager.impl;

import collector.hotwheels.api.model.BaseItem;
import collector.hotwheels.insight.imports.ImportConfiguration;
import collector.hotwheels.insight.imports.ModuleProperties;
import collector.hotwheels.insight.imports.manager.AbstractService;
import collector.hotwheels.insight.imports.model.locators.StatusLocator;
import collector.hotwheels.insight.imports.model.locators.base.ModuleDataLocator;
import collector.hotwheels.insight.imports.model.locators.base.StandardDataLocator;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.DataLocator;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.ModuleOTSelector;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static collector.hotwheels.insight.util.StringUtils.isNullOrEmpty;

public abstract class AbstractBaseService extends AbstractService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractBaseService.class);

    public final static ModuleDataLocator status = new StatusLocator(ModuleProperties.STATUS);
    public final static ModuleDataLocator id = new StandardDataLocator(ModuleProperties.ID, "id");
    public final static ModuleDataLocator name = new StandardDataLocator(ModuleProperties.NAME, "name");

    protected List<ModuleDataLocator> dataLocators = new ArrayList<>(Arrays.asList(status, id, name));

    protected AbstractBaseService(ImportConfiguration configuration,
            List<DataLocator> configuredDataLocators,
            List<ModuleOTSelector> enabledModuleOTSelectors) {
        super(configuration, configuredDataLocators, enabledModuleOTSelectors);
    }

    protected byte[] getAvatarBytes(BaseItem baseItem) {
        byte[] avatarBytes = null;

        try {
            if (baseItem != null && !isNullOrEmpty(baseItem.getImageFileName())) {
                File image = Paths.get(configuration.getDataFolder(), baseItem.getImageFileName())
                        .toFile();
                if (image.exists()) {
                    avatarBytes = Files.readAllBytes(image.toPath());
                }
            }
        } catch (Exception ex) {
            logger.error("Error reading image bytes for: " + baseItem.getImageFileName());
        }

        return avatarBytes;
    }

}
