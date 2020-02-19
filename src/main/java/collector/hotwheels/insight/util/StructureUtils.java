package collector.hotwheels.insight.util;

import com.riadalabs.jira.plugins.insight.services.imports.common.external.DataLocator;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.ObjectTypeAttributeModuleExternal;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.ReferencedObjectTypeAttributeModuleExternal;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.external.baseversion.InsightSchemaExternal;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.external.baseversion.ObjectTypeExternal;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.external.baseversion.ReferenceTypeExternal;
import com.riadalabs.jira.plugins.insight.services.model.ObjectTypeAttributeBean;

public final class StructureUtils {

    //region TextObjectType

    public static void addTextObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            String description,
            boolean hidden) {
        addTextObjectTypeAttribute(name, objectTypeExternal, dataLocator, false, false, false, description, hidden);
    }

    public static void addTextObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            String description) {
        addTextObjectTypeAttribute(name, objectTypeExternal, dataLocator, false, false, false, description);
    }

    public static void addTextObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator) {
        addTextObjectTypeAttribute(name, objectTypeExternal, dataLocator, false, false, false);
    }

    public static void addTextObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            boolean label,
            boolean unique,
            boolean identifier) {

        addTextObjectTypeAttribute(name, objectTypeExternal, dataLocator, label, unique, identifier, null);
    }

    public static void addTextObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            boolean label,
            boolean unique,
            boolean identifier,
            String description) {

        addTextObjectTypeAttribute(name, objectTypeExternal, dataLocator, label, unique, identifier, description,
                false);
    }

    public static void addTextObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            boolean label,
            boolean unique,
            boolean identifier,
            String description,
            boolean hidden) {

        ObjectTypeAttributeModuleExternal ota = new ObjectTypeAttributeModuleExternal(dataLocator, identifier);
        ota.setType(ObjectTypeAttributeBean.Type.DEFAULT.getTypeId());
        ota.setDefaultTypeId(ObjectTypeAttributeBean.DefaultType.TEXT.getDefaultTypeId());
        ota.setName(name);
        ota.setLabel(label);
        ota.setUniqueAttribute(unique);
        ota.setDescription(description);
        ota.setHidden(hidden);

        objectTypeExternal.getObjectTypeAttributes()
                .add(ota);
    }

    //endregion

    //region TextAreaObjectType

    public static void addTextAreaObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            String description) {
        addTextAreaObjectTypeAttribute(name, objectTypeExternal, dataLocator, false, false, description);
    }

    public static void addTextAreaObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator) {
        addTextAreaObjectTypeAttribute(name, objectTypeExternal, dataLocator, false);
    }

    public static void addTextAreaObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            boolean unique) {
        addTextAreaObjectTypeAttribute(name, objectTypeExternal, dataLocator, unique, false);
    }

    public static void addTextAreaObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            boolean unique,
            boolean identifier) {
        addTextAreaObjectTypeAttribute(name, objectTypeExternal, dataLocator, unique, identifier, null);
    }

    public static void addTextAreaObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            boolean unique,
            boolean identifier,
            String description) {

        ObjectTypeAttributeModuleExternal ota = new ObjectTypeAttributeModuleExternal(dataLocator, identifier);
        ota.setType(ObjectTypeAttributeBean.Type.DEFAULT.getTypeId());
        ota.setDefaultTypeId(ObjectTypeAttributeBean.DefaultType.TEXTAREA.getDefaultTypeId());
        ota.setName(name);
        ota.setUniqueAttribute(unique);
        ota.setDescription(description);

        objectTypeExternal.getObjectTypeAttributes()
                .add(ota);
    }

    //endregion

    //region ValueObjectType

    public static void addValueObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            ObjectTypeAttributeBean.DefaultType defaultType,
            String suffix,
            String description) {
        addValueObjectTypeAttribute(name, objectTypeExternal, dataLocator, defaultType, suffix, false, false,
                description);
    }

    public static void addValueObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            ObjectTypeAttributeBean.DefaultType defaultType) {
        addValueObjectTypeAttribute(name, objectTypeExternal, dataLocator, defaultType, null);
    }

    public static void addValueObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            ObjectTypeAttributeBean.DefaultType defaultType,
            String suffix) {
        addValueObjectTypeAttribute(name, objectTypeExternal, dataLocator, defaultType, suffix, false);
    }

    public static void addValueObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            ObjectTypeAttributeBean.DefaultType defaultType,
            String suffix,
            boolean summable) {
        addValueObjectTypeAttribute(name, objectTypeExternal, dataLocator, defaultType, suffix, summable, false);
    }

    public static void addValueObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            ObjectTypeAttributeBean.DefaultType defaultType,
            String suffix,
            boolean summable,
            boolean identifier) {
        addValueObjectTypeAttribute(name, objectTypeExternal, dataLocator, defaultType, suffix, summable, identifier,
                null);
    }

    public static void addValueObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            ObjectTypeAttributeBean.DefaultType defaultType,
            String suffix,
            boolean summable,
            boolean identifier,
            String description) {
        ObjectTypeAttributeModuleExternal ota = new ObjectTypeAttributeModuleExternal(dataLocator, identifier);
        ota.setType(ObjectTypeAttributeBean.Type.DEFAULT.getTypeId());
        ota.setDefaultTypeId(defaultType.getDefaultTypeId());
        ota.setName(name);
        if (suffix != null) {
            ota.setSuffix(suffix);
        }
        ota.setSummable(summable);
        ota.setDescription(description);

        objectTypeExternal.getObjectTypeAttributes()
                .add(ota);
    }

    //endregion

    //region BooleanObjectType

    public static void addBooleanObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            String description) {

        addBooleanObjectTypeAttribute(name, objectTypeExternal, dataLocator, false, description);
    }

    public static void addBooleanObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator) {

        addBooleanObjectTypeAttribute(name, objectTypeExternal, dataLocator, false);
    }

    public static void addBooleanObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            boolean identifier) {

        addBooleanObjectTypeAttribute(name, objectTypeExternal, dataLocator, identifier, null);
    }

    public static void addBooleanObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            boolean identifier,
            String description) {

        ObjectTypeAttributeModuleExternal ota = new ObjectTypeAttributeModuleExternal(dataLocator, identifier);
        ota.setType(ObjectTypeAttributeBean.Type.DEFAULT.getTypeId());
        ota.setDefaultTypeId(ObjectTypeAttributeBean.DefaultType.BOOLEAN.getDefaultTypeId());
        ota.setName(name);
        ota.setDescription(description);

        objectTypeExternal.getObjectTypeAttributes()
                .add(ota);
    }
    //endregion

    //region SelectObjectType
    public static void addSelectObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            String description) {

        addSelectObjectTypeAttribute(name, objectTypeExternal, dataLocator, false, description);
    }

    public static void addSelectObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator) {
        addSelectObjectTypeAttribute(name, objectTypeExternal, dataLocator, false);
    }

    public static void addSelectObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            boolean identifier) {

        addSelectObjectTypeAttribute(name, objectTypeExternal, dataLocator, identifier, null);
    }

    public static void addSelectObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            boolean identifier,
            String description) {

        ObjectTypeAttributeModuleExternal ota = new ObjectTypeAttributeModuleExternal(dataLocator, identifier);
        ota.setType(ObjectTypeAttributeBean.Type.DEFAULT.getTypeId());
        ota.setDefaultTypeId(ObjectTypeAttributeBean.DefaultType.SELECT.getDefaultTypeId());
        ota.setName(name);
        ota.setDescription(description);

        objectTypeExternal.getObjectTypeAttributes()
                .add(ota);
    }
    //endregion

    //region URLObjectType

    public static void addDateTimeObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            String description) {
        addDateObjectTypeAttribute(name, objectTypeExternal, dataLocator, ObjectTypeAttributeBean.DefaultType.DATE_TIME,
                false, description);
    }

    public static void addDateTimeObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator) {
        addDateObjectTypeAttribute(name, objectTypeExternal, dataLocator,
                ObjectTypeAttributeBean.DefaultType.DATE_TIME);
    }

    public static void addDateObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            String description) {
        addDateObjectTypeAttribute(name, objectTypeExternal, dataLocator, ObjectTypeAttributeBean.DefaultType.DATE,
                false, description);
    }

    public static void addDateObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator) {
        addDateObjectTypeAttribute(name, objectTypeExternal, dataLocator, ObjectTypeAttributeBean.DefaultType.DATE);
    }

    public static void addDateObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            ObjectTypeAttributeBean.DefaultType defaultType) {
        addDateObjectTypeAttribute(name, objectTypeExternal, dataLocator, defaultType, false);
    }

    public static void addDateObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            ObjectTypeAttributeBean.DefaultType defaultType,
            boolean identifier) {
        addDateObjectTypeAttribute(name, objectTypeExternal, dataLocator, defaultType, identifier, null);
    }

    public static void addDateObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            ObjectTypeAttributeBean.DefaultType defaultType,
            boolean identifier,
            String description) {
        ObjectTypeAttributeModuleExternal ota = new ObjectTypeAttributeModuleExternal(dataLocator, identifier);
        ota.setType(ObjectTypeAttributeBean.Type.DEFAULT.getTypeId());
        ota.setDefaultTypeId(defaultType.getDefaultTypeId());
        ota.setName(name);
        ota.setDescription(description);

        objectTypeExternal.getObjectTypeAttributes()
                .add(ota);
    }
    //endregion

    //region URLObjectType

    public static void addURLObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            String description) {
        addURLObjectTypeAttribute(name, objectTypeExternal, dataLocator, false, description);
    }

    public static void addURLObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator) {
        addURLObjectTypeAttribute(name, objectTypeExternal, dataLocator, false);
    }

    public static void addURLObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            boolean identifier) {
        addURLObjectTypeAttribute(name, objectTypeExternal, dataLocator, identifier, null);
    }

    public static void addURLObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            boolean identifier,
            String description) {

        ObjectTypeAttributeModuleExternal ota = new ObjectTypeAttributeModuleExternal(dataLocator, identifier);
        ota.setType(ObjectTypeAttributeBean.Type.DEFAULT.getTypeId());
        ota.setDefaultTypeId(ObjectTypeAttributeBean.DefaultType.URL.getDefaultTypeId());
        ota.setName(name);
        ota.setDescription(description);

        objectTypeExternal.getObjectTypeAttributes()
                .add(ota);
    }
    //endregion

    //region StatusObjectType

    public static void addStatusObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            String description) {
        addStatusObjectTypeAttribute(name, objectTypeExternal, dataLocator, false, description);
    }

    public static void addStatusObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator) {
        addStatusObjectTypeAttribute(name, objectTypeExternal, dataLocator, false);
    }

    public static void addStatusObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            boolean identifier) {
        addStatusObjectTypeAttribute(name, objectTypeExternal, dataLocator, identifier, null);
    }

    public static void addStatusObjectTypeAttribute(String name,
            ObjectTypeExternal objectTypeExternal,
            DataLocator dataLocator,
            boolean identifier,
            String description) {
        ObjectTypeAttributeModuleExternal ota = new ObjectTypeAttributeModuleExternal(dataLocator, identifier);
        ota.setType(ObjectTypeAttributeBean.Type.STATUS.getTypeId());
        ota.setName(name);
        ota.setDescription(description);

        objectTypeExternal.getObjectTypeAttributes()
                .add(ota);
    }
    //endregion

    //region ReferenceObjectType
    public static void addReferenceObjectTypeAttribute(InsightSchemaExternal insightSchemaExternal,
            String name,
            ObjectTypeExternal objectTypeExternal,
            ObjectTypeExternal referenceObjectTypeExternal,
            String referenceTypeName,
            boolean multiple,
            boolean includeChilds,
            DataLocator dataLocator,
            boolean identifier,
            String mappingIQL,
            String description,
            int referenceTypeSequenceNumber,
            int objectSchemaId) {

        ObjectTypeAttributeModuleExternal ota =
                new ReferencedObjectTypeAttributeModuleExternal(dataLocator, identifier, mappingIQL);
        ota.setType(ObjectTypeAttributeBean.Type.REFERENCED_OBJECT.getTypeId());
        ota.setName(name);
        ota.setReferenceObjectTypeId(referenceObjectTypeExternal.getId());

        ReferenceTypeExternal referenceTypeExternal = getReferenceTypeExternal(referenceTypeName, objectSchemaId);
        referenceTypeExternal.setId(referenceTypeSequenceNumber);
        insightSchemaExternal.getReferenceTypes()
                .add(referenceTypeExternal);

        ota.setReferenceType(referenceTypeExternal);
        ota.setIncludeChildObjectTypes(includeChilds);
        if (multiple) {
            ota.setMaximumCardinality(ObjectTypeAttributeBean.CARDINALITY_MAXIMUM_UNLIMITED);
        }
        ota.setDescription(description);

        objectTypeExternal.getObjectTypeAttributes()
                .add(ota);
    }

    public static ReferenceTypeExternal getReferenceTypeExternal(String name, int objectSchemaId) {
        ReferenceTypeExternal referenceTypeExternal = new ReferenceTypeExternal();
        referenceTypeExternal.setName(name);
        referenceTypeExternal.setObjectSchemaId(objectSchemaId);
        return referenceTypeExternal;
    }
    //endregion
}
