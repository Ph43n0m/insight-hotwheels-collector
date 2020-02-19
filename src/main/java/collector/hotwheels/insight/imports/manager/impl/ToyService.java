package collector.hotwheels.insight.imports.manager.impl;

import collector.hotwheels.api.model.TableEntry;
import collector.hotwheels.insight.imports.ImportConfiguration;
import collector.hotwheels.insight.imports.ModuleProperties;
import collector.hotwheels.insight.imports.manager.ClientProvider;
import collector.hotwheels.insight.imports.manager.IData;
import collector.hotwheels.insight.imports.manager.ITypeManager;
import collector.hotwheels.insight.imports.model.locators.ToyCollectionLocator;
import collector.hotwheels.insight.imports.model.locators.ToyNotesLocator;
import collector.hotwheels.insight.imports.model.locators.ToyOtherAttributesLocator;
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

public class ToyService extends AbstractBaseService implements ITypeManager, IData {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(ToyService.class);

    public final static ModuleDataLocator treasureHunt =
            new StandardDataLocator(ModuleProperties.TREASURE_HUNT, "treasureHunt");
    public final static ModuleDataLocator superTreasureHunt =
            new StandardDataLocator(ModuleProperties.SUPER_TREASURE_HUNT, "superTreasureHunt");
    public final static ModuleDataLocator collectionNumber =
            new StandardDataLocator(ModuleProperties.COLLECTION_NUMBER, "collectionNumber");
    public final static ModuleDataLocator toyNumber = new StandardDataLocator(ModuleProperties.TOY_NUMBER, "toyNumber");
    public final static ModuleDataLocator modelName = new StandardDataLocator(ModuleProperties.MODEL, "modelName");
    public final static ModuleDataLocator casting = new StandardDataLocator(ModuleProperties.CASTING, "parentPageId");
    public final static ModuleDataLocator realName = new StandardDataLocator(ModuleProperties.REAL_NAME, "realName");
    public final static ModuleDataLocator year = new StandardDataLocator(ModuleProperties.YEAR, "year");
    public final static ModuleDataLocator series = new StandardDataLocator(ModuleProperties.SERIES, "series");
    public final static ModuleDataLocator scale = new StandardDataLocator(ModuleProperties.SCALE, "scale");
    public final static ModuleDataLocator wheelType = new StandardDataLocator(ModuleProperties.WHEEL_TYPE, "wheelType");
    public final static ModuleDataLocator country = new StandardDataLocator(ModuleProperties.COUNTRY, "country");
    public final static ModuleDataLocator sticker = new StandardDataLocator(ModuleProperties.STICKER, "sticker");
    public final static ModuleDataLocator tampo = new StandardDataLocator(ModuleProperties.TAMPO, "tampo");
    public final static ModuleDataLocator origin = new StandardDataLocator(ModuleProperties.ORIGIN, "origin");
    public final static ModuleDataLocator policeDept =
            new StandardDataLocator(ModuleProperties.POLICE_DEPARTMENT, "policeDept");
    public final static ModuleDataLocator quantity = new StandardDataLocator(ModuleProperties.QUANTITY, "quantity");
    public final static ModuleDataLocator color = new StandardDataLocator(ModuleProperties.COLOR, "color");
    public final static ModuleDataLocator baseColorType =
            new StandardDataLocator(ModuleProperties.BASE_COLOR_TYPE, "baseColorType");
    public final static ModuleDataLocator windowColor =
            new StandardDataLocator(ModuleProperties.WINDOW_COLOR, "windowColor");
    public final static ModuleDataLocator interiorColor =
            new StandardDataLocator(ModuleProperties.INTERIOR_COLOR, "interiorColor");
    public final static ModuleDataLocator wheelColor =
            new StandardDataLocator(ModuleProperties.WHEEL_COLOR, "wheelColor");
    public final static ModuleDataLocator engineColor =
            new StandardDataLocator(ModuleProperties.ENGINE_COLOR, "engineColor");
    public final static ModuleDataLocator roofColor = new StandardDataLocator(ModuleProperties.ROOF_COLOR, "roofColor");
    public final static ModuleDataLocator bladeColor =
            new StandardDataLocator(ModuleProperties.BLADE_COLOR, "bladeColor");
    public final static ModuleDataLocator propellerColor =
            new StandardDataLocator(ModuleProperties.PROPELLER_COLOR, "propellerColor");
    public final static ModuleDataLocator blimpColor =
            new StandardDataLocator(ModuleProperties.BLIMP_COLOR, "blimpColor");
    public final static ModuleDataLocator wingColor =
            new StandardDataLocator(ModuleProperties.WINDOW_COLOR, "wingColor");
    public final static ModuleDataLocator riderColor =
            new StandardDataLocator(ModuleProperties.RIDER_COLOR, "riderColor");
    public final static ModuleDataLocator driver = new StandardDataLocator(ModuleProperties.DRIVER, "driver");
    public final static ModuleDataLocator record = new StandardDataLocator(ModuleProperties.RECORD, "record");
    public final static ModuleDataLocator plate = new StandardDataLocator(ModuleProperties.PLATE, "plate");
    public final static ModuleDataLocator image = new StandardDataLocator(ModuleProperties.IMAGE, "image");

    public final static ModuleDataLocator collections = new ToyCollectionLocator(ModuleProperties.COLLECTIONS);

    public final static ModuleDataLocator otherAttributes =
            new ToyOtherAttributesLocator(ModuleProperties.OTHER_ATTRIBUTES);
    public final static ModuleDataLocator notes = new ToyNotesLocator(ModuleProperties.NOTES);

    // TODO: ref for wheel type (can contain color)
    // TODO: ref for series (can contain a number)

    public ToyService(ImportConfiguration configuration, List<DataLocator> configuredDataLocators,
            List<ModuleOTSelector> enabledModuleOTSelectors) {
        super(configuration, configuredDataLocators, enabledModuleOTSelectors);
        dataLocators.add(casting);
        dataLocators.add(treasureHunt);
        dataLocators.add(superTreasureHunt);
        dataLocators.add(collectionNumber);
        dataLocators.add(toyNumber);
        dataLocators.add(realName);
        dataLocators.add(modelName);
        dataLocators.add(year);
        dataLocators.add(series);
        dataLocators.add(scale);
        dataLocators.add(wheelType);
        dataLocators.add(country);
        dataLocators.add(notes);
        dataLocators.add(sticker);
        dataLocators.add(tampo);
        dataLocators.add(origin);
        dataLocators.add(policeDept);
        dataLocators.add(quantity);
        dataLocators.add(color);
        dataLocators.add(baseColorType);
        dataLocators.add(windowColor);
        dataLocators.add(interiorColor);
        dataLocators.add(wheelColor);
        dataLocators.add(engineColor);
        dataLocators.add(roofColor);
        dataLocators.add(bladeColor);
        dataLocators.add(propellerColor);
        dataLocators.add(blimpColor);
        dataLocators.add(wingColor);
        dataLocators.add(riderColor);
        dataLocators.add(driver);
        dataLocators.add(record);
        dataLocators.add(plate);
        dataLocators.add(image);
        dataLocators.add(otherAttributes);
        dataLocators.add(collections);

    }

    @Override
    public List<DataLocator> getDataLocators() {
        return new ArrayList<>(dataLocators);
    }

    @SuppressWarnings ("unchecked")
    @Override
    public ImportDataValues getDataHolder() {

        List<ImportDataValue> dataMap = new ArrayList<>();

        for (TableEntry obj : getData()) {
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
                                    ((ReferencedDataLocator<TableEntry, Object>) moduleDataLocator).getReferencedLocatorValue(
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
    public List<TableEntry> getData() {
        logger.debug("Fetching Toy Data.");

        Set<TableEntry> ret = new HashSet<>();
        try {
            wikiaClient = ClientProvider.INSTANCE.getClient(configuration);
            if (wikiaClient != null) {
                ret = wikiaClient.toys()
                        .list()
                        .execute();
            }
        } catch (Exception ex) {
            logger.error("Error fetch Toy data", ex);
        }

        return ret.isEmpty() ? null : new ArrayList<>(ret);
    }

}
