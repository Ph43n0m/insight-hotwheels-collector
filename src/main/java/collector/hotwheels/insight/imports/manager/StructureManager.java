package collector.hotwheels.insight.imports.manager;

import collector.hotwheels.insight.imports.ImportConfiguration;
import collector.hotwheels.insight.imports.ModuleProperties;
import collector.hotwheels.insight.imports.manager.impl.AbstractBaseService;
import collector.hotwheels.insight.imports.manager.impl.CollectionService;
import collector.hotwheels.insight.imports.manager.impl.ModelService;
import collector.hotwheels.insight.imports.manager.impl.ToyService;
import collector.hotwheels.insight.imports.model.ModuleSelector;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.DataLocator;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.ModuleOTSelector;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.MissingObjectsType;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.ObjectTypeModuleExternal;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.TemplateHandleMissingObjects;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.ThresholdType;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.external.baseversion.IconExternal;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.external.baseversion.InsightSchemaExternal;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.external.baseversion.ObjectSchemaExternal;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.external.baseversion.ObjectTypeExternal;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.external.baseversion.ReferenceTypeExternal;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.external.baseversion.StatusTypeExternal;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static collector.hotwheels.insight.util.StructureUtils.addBooleanObjectTypeAttribute;
import static collector.hotwheels.insight.util.StructureUtils.addReferenceObjectTypeAttribute;
import static collector.hotwheels.insight.util.StructureUtils.addStatusObjectTypeAttribute;
import static collector.hotwheels.insight.util.StructureUtils.addTextAreaObjectTypeAttribute;
import static collector.hotwheels.insight.util.StructureUtils.addTextObjectTypeAttribute;

public class StructureManager extends AbstractService {

    private final Logger logger = LoggerFactory.getLogger(StructureManager.class);

    private static final int OBJECT_SCHEMA_ID = 1; /* Just a fake id */
    private int objectTypeSequenceNumber;
    private int iconSequenceNumber;
    private int referenceTypeSequenceNumber;

    public StructureManager(ImportConfiguration configuration) {
        super(configuration, null, null);

        this.objectTypeSequenceNumber = 1;
        this.iconSequenceNumber = 1;
        this.referenceTypeSequenceNumber = 1;
    }

    public InsightSchemaExternal getPredefinedStructure() {

        InsightSchemaExternal insightSchemaExternal = new InsightSchemaExternal();

        ObjectSchemaExternal objectSchemaExternal = new ObjectSchemaExternal();
        insightSchemaExternal.setObjectSchema(objectSchemaExternal);
        objectSchemaExternal.setId(OBJECT_SCHEMA_ID);

        List<StatusTypeExternal> statusTypes = new ArrayList<>();
        for (ModuleProperties.Status status : ModuleProperties.Status.values()) {
            StatusTypeExternal statusTypeExternal = new StatusTypeExternal();
            statusTypeExternal.setName(status.getName());
            statusTypeExternal.setCategory(status.getCategory());
            statusTypeExternal.setObjectSchemaId(OBJECT_SCHEMA_ID);
            statusTypes.add(statusTypeExternal);
        }

        insightSchemaExternal.setStatusTypes(statusTypes);

        /* How to handle missing objects update 0 sync */
        TemplateHandleMissingObjects handleMissingObjectsUpdate =
                new TemplateHandleMissingObjects(MissingObjectsType.UPDATE);
        handleMissingObjectsUpdate.setThreshold(0);
        handleMissingObjectsUpdate.setThresholdType(ThresholdType.RUNS);
        handleMissingObjectsUpdate.setObjectTypeAttributeName(ModuleProperties.STATUS);
        handleMissingObjectsUpdate.setNewAttributeValue(ModuleProperties.Status.NOT_FOUND.getName());

        /* How to handle missing objects remove 0 sync */
        TemplateHandleMissingObjects handleMissingObjectsRemoveOneSync =
                new TemplateHandleMissingObjects(MissingObjectsType.REMOVE);
        handleMissingObjectsRemoveOneSync.setThreshold(0);
        handleMissingObjectsRemoveOneSync.setThresholdType(ThresholdType.RUNS);

        /* HOT_WHEELS_ROOT Root Object */
        ObjectTypeExternal hotWheelsRootObjectTypeExternal =
                appendObjectTypeExternal(ModuleProperties.HOT_WHEELS_ROOT, null, insightSchemaExternal, null, null,
                        false, true);
        objectSchemaExternal.getObjectTypes()
                .add(hotWheelsRootObjectTypeExternal);

        /* CATEGORY Object */
        ObjectTypeExternal categoryObjectTypeExternal =
                appendObjectTypeExternal(ModuleProperties.CATEGORY, hotWheelsRootObjectTypeExternal,
                        insightSchemaExternal, ModuleSelector.CATEGORY, handleMissingObjectsUpdate, true, true);

        /* COLLECTION Object */
        ObjectTypeExternal collectionObjectTypeExternal =
                appendObjectTypeExternal(ModuleProperties.COLLECTION, hotWheelsRootObjectTypeExternal,
                        insightSchemaExternal, ModuleSelector.COLLECTION, handleMissingObjectsUpdate, true, true);


        /* MODEL Object */
        ObjectTypeExternal modelObjectTypeExternal =
                appendObjectTypeExternal(ModuleProperties.MODEL, hotWheelsRootObjectTypeExternal, insightSchemaExternal,
                        ModuleSelector.MODEL, handleMissingObjectsUpdate, true, true);

        /* TOY Object */
        ObjectTypeExternal toyObjectTypeExternal =
                appendObjectTypeExternal(ModuleProperties.TOY, hotWheelsRootObjectTypeExternal, insightSchemaExternal,
                        ModuleSelector.TOY, handleMissingObjectsUpdate, true, true);

        addCategoryAttributes(insightSchemaExternal, categoryObjectTypeExternal);
        addCollectionAttributes(insightSchemaExternal, collectionObjectTypeExternal, categoryObjectTypeExternal,
                modelObjectTypeExternal);
        addModelAttributes(insightSchemaExternal, modelObjectTypeExternal, categoryObjectTypeExternal);
        addToyAttributes(insightSchemaExternal, toyObjectTypeExternal, modelObjectTypeExternal,
                collectionObjectTypeExternal);

        return insightSchemaExternal;
    }

    private void addCollectionAttributes(InsightSchemaExternal insightSchemaExternal,
            ObjectTypeExternal objectType,
            ObjectTypeExternal categoryObjectType,
            ObjectTypeExternal modelObjectType) {
        addBaseAttributes(objectType);

        addTextAreaObjectTypeAttribute(ModuleProperties.DESCRIPTION, objectType, CollectionService.description);

        // MODELS Reference
        addReferenceAttribute(insightSchemaExternal, ModuleProperties.MODELS, objectType, modelObjectType,
                ModuleProperties.REF_NAME_BELONGS, true, false, CollectionService.models, false,
                "\"" + ModuleProperties.NAME + "\" IN (${" + CollectionService.models.getLocator() + "${0}})", null);

        // CATEGORIES Reference
        addReferenceAttribute(insightSchemaExternal, ModuleProperties.CATEGORIES, objectType, categoryObjectType,
                ModuleProperties.REF_NAME_BELONGS, true, false, CollectionService.categories, false,
                "\"" + ModuleProperties.ID + "\" IN (${" + CollectionService.categories.getLocator() + "${0}})", null);

    }

    private void addToyAttributes(InsightSchemaExternal insightSchemaExternal,
            ObjectTypeExternal objectType,
            ObjectTypeExternal modelObjectType,
            ObjectTypeExternal collectionObjectType) {
        addBaseAttributes(objectType);

        addBooleanObjectTypeAttribute(ModuleProperties.TREASURE_HUNT, objectType, ToyService.treasureHunt);
        addBooleanObjectTypeAttribute(ModuleProperties.SUPER_TREASURE_HUNT, objectType, ToyService.superTreasureHunt);

        // MODEL Reference
        addReferenceAttribute(insightSchemaExternal, ModuleProperties.MODEL, objectType, modelObjectType,
                ModuleProperties.REF_NAME_BASED, false, false, ToyService.modelName, false,
                "\"" + ModuleProperties.NAME + "\" = ${" + ToyService.modelName.getLocator() + "}",
                "The model where this version is based on.");

        addTextObjectTypeAttribute(ModuleProperties.COLLECTION_NUMBER, objectType, ToyService.collectionNumber);
        addTextObjectTypeAttribute(ModuleProperties.TOY_NUMBER, objectType, ToyService.toyNumber);
        addTextObjectTypeAttribute(ModuleProperties.YEAR, objectType, ToyService.year);
        addTextObjectTypeAttribute(ModuleProperties.SERIES, objectType, ToyService.series); // TODO: ref ????
        addTextObjectTypeAttribute(ModuleProperties.WHEEL_TYPE, objectType, ToyService.wheelType);
        addTextObjectTypeAttribute(ModuleProperties.WHEEL_COLOR, objectType, ToyService.wheelColor);
        addTextObjectTypeAttribute(ModuleProperties.COUNTRY, objectType, ToyService.country);
        addTextObjectTypeAttribute(ModuleProperties.COLOR, objectType, ToyService.color);
        addTextObjectTypeAttribute(ModuleProperties.BASE_COLOR_TYPE, objectType, ToyService.baseColorType);
        addTextObjectTypeAttribute(ModuleProperties.TAMPO, objectType, ToyService.tampo);
        addTextObjectTypeAttribute(ModuleProperties.WINDOW_COLOR, objectType, ToyService.windowColor);
        addTextObjectTypeAttribute(ModuleProperties.INTERIOR_COLOR, objectType, ToyService.interiorColor);
        addTextObjectTypeAttribute(ModuleProperties.ENGINE_COLOR, objectType, ToyService.engineColor);
        addTextObjectTypeAttribute(ModuleProperties.ROOF_COLOR, objectType, ToyService.roofColor);
        addTextObjectTypeAttribute(ModuleProperties.BLADE_COLOR, objectType, ToyService.bladeColor);
        addTextObjectTypeAttribute(ModuleProperties.BLIMP_COLOR, objectType, ToyService.blimpColor);
        addTextObjectTypeAttribute(ModuleProperties.WING_COLOR, objectType, ToyService.wingColor);
        addTextObjectTypeAttribute(ModuleProperties.RIDER_COLOR, objectType, ToyService.riderColor);

        addTextObjectTypeAttribute(ModuleProperties.STICKER, objectType, ToyService.sticker);
        addTextObjectTypeAttribute(ModuleProperties.SCALE, objectType, ToyService.scale);
        addTextObjectTypeAttribute(ModuleProperties.REAL_NAME, objectType, ToyService.realName);
        addTextObjectTypeAttribute(ModuleProperties.ORIGIN, objectType, ToyService.origin); // TODO: is this a ref ?
        addTextObjectTypeAttribute(ModuleProperties.POLICE_DEPARTMENT, objectType, ToyService.policeDept);
        addTextObjectTypeAttribute(ModuleProperties.QUANTITY, objectType, ToyService.quantity);
        addTextObjectTypeAttribute(ModuleProperties.DRIVER, objectType, ToyService.driver);
        addTextObjectTypeAttribute(ModuleProperties.RECORD, objectType, ToyService.record);
        addTextObjectTypeAttribute(ModuleProperties.PLATE, objectType, ToyService.plate);

        addTextObjectTypeAttribute(ModuleProperties.IMAGE, objectType, ToyService.image);

        addTextAreaObjectTypeAttribute(ModuleProperties.OTHER_ATTRIBUTES, objectType, ToyService.otherAttributes);
        addTextAreaObjectTypeAttribute(ModuleProperties.NOTES, objectType, ToyService.notes);

        // COLLECTIONS Reference
        addReferenceAttribute(insightSchemaExternal, ModuleProperties.COLLECTIONS, objectType, collectionObjectType,
                ModuleProperties.REF_NAME_BELONGS, true, false, ToyService.collections, false,
                "\"" + ModuleProperties.NAME + "\" IN (${" + ToyService.collections.getLocator() + "${0}})",
                "The collections where this toy belongs to.");

        // CASTING Reference
        addReferenceAttribute(insightSchemaExternal, ModuleProperties.CASTING, objectType, modelObjectType,
                ModuleProperties.REF_NAME_BASED, false, false, ToyService.casting, false,
                "\"" + ModuleProperties.ID + "\" = ${" + ToyService.casting.getLocator() + "}",
                "The model where this version is based on.");

    }

    private void addModelAttributes(InsightSchemaExternal insightSchemaExternal,
            ObjectTypeExternal objectType,
            ObjectTypeExternal categoryObjectType) {
        addBaseAttributes(objectType);

        addTextAreaObjectTypeAttribute(ModuleProperties.DESCRIPTION, objectType, ModelService.description);
        addTextObjectTypeAttribute(ModuleProperties.DEBUT_SERIES, objectType, ModelService.debutSeries);
        addTextObjectTypeAttribute(ModuleProperties.PRODUCING_YEARS, objectType, ModelService.producingYears);
        addTextObjectTypeAttribute(ModuleProperties.DESIGNER, objectType, ModelService.designer);
        addTextObjectTypeAttribute(ModuleProperties.TOY_NUMBER, objectType, ModelService.toyNumber);
        addTextObjectTypeAttribute(ModuleProperties.PRODUCTION, objectType, ModelService.production);
        addTextObjectTypeAttribute(ModuleProperties.CAR_DESIGNER, objectType, ModelService.carDesigner);
        addTextObjectTypeAttribute(ModuleProperties.ENGINE, objectType, ModelService.engine);
        addTextObjectTypeAttribute(ModuleProperties.HORSEPOWER, objectType, ModelService.horsepower);
        addTextObjectTypeAttribute(ModuleProperties.TOP_SPEED, objectType, ModelService.topSpeed);
        addTextObjectTypeAttribute(ModuleProperties.ACCELERATION, objectType, ModelService.acceleration);
        addTextObjectTypeAttribute(ModuleProperties.BORN, objectType, ModelService.born);
        addTextObjectTypeAttribute(ModuleProperties.BIRTHPLACE, objectType, ModelService.birthplace);
        addTextAreaObjectTypeAttribute(ModuleProperties.SPECIALTY, objectType, ModelService.specialty);

        // CATEGORIES Reference
        addReferenceAttribute(insightSchemaExternal, ModuleProperties.CATEGORIES, objectType, categoryObjectType,
                ModuleProperties.REF_NAME_BELONGS, true, false, ModelService.categories, false,
                "\"" + ModuleProperties.ID + "\" IN (${" + ModelService.categories.getLocator() + "${0}})", null);

    }

    private void addCategoryAttributes(InsightSchemaExternal insightSchemaExternal, ObjectTypeExternal objectType) {
        addBaseAttributes(objectType);

    }

    private void addBaseAttributes(ObjectTypeExternal objectType) {

        addStatusObjectTypeAttribute(ModuleProperties.STATUS, objectType, AbstractBaseService.status);
        addTextObjectTypeAttribute(ModuleProperties.ID, objectType, AbstractBaseService.id, false, false, true);
        addTextObjectTypeAttribute(ModuleProperties.NAME, objectType, AbstractBaseService.name, true, false, true);

    }

    private ObjectTypeModuleExternal appendObjectTypeExternal(String name,
            ObjectTypeExternal parentObjectTypeExternal,
            InsightSchemaExternal insightSchemaExternal,
            ModuleSelector selector,
            TemplateHandleMissingObjects handleMissingObjects,
            boolean createOTconfiguration,
            boolean enabledByDefault) {

        ObjectTypeModuleExternal objectTypeExternal =
                new ObjectTypeModuleExternal(selector != null ? selector.getSelector() : null, createOTconfiguration,
                        handleMissingObjects, !enabledByDefault);
        objectTypeExternal.setId(this.objectTypeSequenceNumber++);
        objectTypeExternal.setName(name);
        objectTypeExternal.setDescription("");
        objectTypeExternal.setObjectSchemaId(OBJECT_SCHEMA_ID);

        IconExternal iconExternal = getIcon(name);
        iconExternal.setId(this.iconSequenceNumber++);
        insightSchemaExternal.getIcons()
                .add(iconExternal);
        objectTypeExternal.setIcon(iconExternal);

        if (parentObjectTypeExternal != null) {
            parentObjectTypeExternal.getObjectTypeChildren()
                    .add(objectTypeExternal);
        }
        return objectTypeExternal;
    }

    private IconExternal getIcon(String objectTypeName) {
        IconExternal iconExternal = new IconExternal();
        iconExternal.setName(objectTypeName);
        iconExternal.setObjectSchemaId(OBJECT_SCHEMA_ID);

        InputStream in = StructureManager.class.getClassLoader()
                .getResourceAsStream("/images/icons/" + objectTypeName + "-48.png");

        try {
            iconExternal.setImage48(IOUtils.toByteArray(in));
            iconExternal.setImage16(iconExternal.getImage48());
        } catch (Exception ioe) {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }

        return iconExternal;
    }

    private void addReferenceAttribute(InsightSchemaExternal insightSchemaExternal,
            String name,
            ObjectTypeExternal objectTypeExternal,
            ObjectTypeExternal referenceObjectTypeExternal,
            String referenceTypeName,
            boolean multiple,
            boolean includeChilds,
            DataLocator dataLocator,
            boolean identifier,
            String mappingIQL,
            String description) {

        addReferenceObjectTypeAttribute(insightSchemaExternal, name, objectTypeExternal, referenceObjectTypeExternal,
                referenceTypeName, multiple, includeChilds, dataLocator, identifier, mappingIQL, description,
                this.referenceTypeSequenceNumber++, OBJECT_SCHEMA_ID);
    }

    private ReferenceTypeExternal getReferenceTypeExternal(String name) {
        ReferenceTypeExternal referenceTypeExternal = new ReferenceTypeExternal();
        referenceTypeExternal.setName(name);
        referenceTypeExternal.setObjectSchemaId(OBJECT_SCHEMA_ID);
        return referenceTypeExternal;
    }

    public List<DataLocator> getDataLocators(ModuleOTSelector moduleOTSelector) {

        ITypeManager typeManager = getDataTypeManager(moduleOTSelector);
        List<DataLocator> dataLocators = null;
        if (typeManager != null) {
            dataLocators = typeManager.getDataLocators();
        }

        if (dataLocators != null) {
            Collections.sort(dataLocators);
            return dataLocators;
        }

        return new ArrayList<>();
    }
}
