package collector.hotwheels.insight.imports.manager.impl;

import collector.hotwheels.api.model.PageData;
import collector.hotwheels.insight.imports.ImportConfiguration;
import collector.hotwheels.insight.imports.ModuleProperties;
import collector.hotwheels.insight.imports.manager.ClientProvider;
import collector.hotwheels.insight.imports.manager.IData;
import collector.hotwheels.insight.imports.manager.ITypeManager;
import collector.hotwheels.insight.imports.model.locators.ModelCategoriesLocator;
import collector.hotwheels.insight.imports.model.locators.ModelDescriptionLocator;
import collector.hotwheels.insight.imports.model.locators.ModelSpecialtyLocator;
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

public class ModelService extends AbstractBaseService implements ITypeManager, IData {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(ModelService.class);

    public final static ModuleDataLocator description = new ModelDescriptionLocator(ModuleProperties.DESCRIPTION);
    public final static ModuleDataLocator designer = new StandardDataLocator(ModuleProperties.DESIGNER, "designer");
    public final static ModuleDataLocator debutSeries =
            new StandardDataLocator(ModuleProperties.DEBUT_SERIES, "debutSeries");
    public final static ModuleDataLocator producingYears =
            new StandardDataLocator(ModuleProperties.PRODUCING_YEARS, "producingYears");
    public final static ModuleDataLocator toyNumber = new StandardDataLocator(ModuleProperties.TOY_NUMBER, "toyNumber");
    public final static ModuleDataLocator production =
            new StandardDataLocator(ModuleProperties.PRODUCTION, "production");
    public final static ModuleDataLocator horsepower =
            new StandardDataLocator(ModuleProperties.HORSEPOWER, "horsepower");
    public final static ModuleDataLocator topSpeed = new StandardDataLocator(ModuleProperties.TOP_SPEED, "topSpeed");
    public final static ModuleDataLocator engine = new StandardDataLocator(ModuleProperties.ENGINE, "engine");
    public final static ModuleDataLocator acceleration =
            new StandardDataLocator(ModuleProperties.ACCELERATION, "acceleration");
    public final static ModuleDataLocator born = new StandardDataLocator(ModuleProperties.BORN, "born");
    public final static ModuleDataLocator birthplace =
            new StandardDataLocator(ModuleProperties.BIRTHPLACE, "birthplace");
    public final static ModuleDataLocator specialty = new ModelSpecialtyLocator(ModuleProperties.SPECIALTY);
    public final static ModuleDataLocator carDesigner =
            new StandardDataLocator(ModuleProperties.CAR_DESIGNER, "carDesigner");

    public final static ReferencedDataLocator<PageData, Object> categories =
            new ModelCategoriesLocator(ModuleProperties.CATEGORIES);

    public ModelService(ImportConfiguration configuration,
            List<DataLocator> configuredDataLocators,
            List<ModuleOTSelector> enabledModuleOTSelectors) {
        super(configuration, configuredDataLocators, enabledModuleOTSelectors);
        dataLocators.add(categories);
        dataLocators.add(description);
        dataLocators.add(designer);
        dataLocators.add(debutSeries);
        dataLocators.add(producingYears);
        dataLocators.add(toyNumber);
        dataLocators.add(production);
        dataLocators.add(horsepower);
        dataLocators.add(topSpeed);
        dataLocators.add(engine);
        dataLocators.add(acceleration);
        dataLocators.add(born);
        dataLocators.add(birthplace);
        dataLocators.add(specialty);
        dataLocators.add(carDesigner);
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
        logger.debug("Fetching Model Data.");

        Set<PageData> ret = new HashSet<>();
        try {
            wikiaClient = ClientProvider.INSTANCE.getClient(configuration);
            if (wikiaClient != null) {
                ret = wikiaClient.models()
                        .list()
                        .execute();
            }
        } catch (Exception ex) {
            logger.error("Error fetch Model data", ex);
        }

        return ret.isEmpty() ? null : new ArrayList<>(ret);
    }

}
