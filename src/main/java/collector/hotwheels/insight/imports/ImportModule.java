package collector.hotwheels.insight.imports;

import collector.hotwheels.insight.imports.manager.ClientProvider;
import collector.hotwheels.insight.imports.manager.ContentManager;
import collector.hotwheels.insight.imports.manager.StructureManager;
import collector.hotwheels.insight.services.JIRAService;
import collector.hotwheels.insight.services.PropertyManager;
import collector.hotwheels.insight.util.CacheProvider;
import collector.hotwheels.insight.util.I18FactoryFake;
import com.atlassian.jira.license.JiraLicenseManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.AbstractInsightImportModule;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.DataLocator;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.ImportComponentException;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.ImportDataHolder;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.ImportDataValue;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.ImportDataValues;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.InMemoryDataHolder;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.InsightImportModule;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.ModuleOTSelector;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.TemplateImportConfiguration;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.DataEntry;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.ObjectTypeAttributeModuleExternal;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.ObjectTypeModuleExternal;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.ReferencedObjectTypeAttributeModuleExternal;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.SimpleDataEntry;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.external.baseversion.InsightSchemaExternal;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.external.baseversion.ObjectTypeAttributeExternal;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.external.baseversion.ObjectTypeExternal;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.model.validation.ValidationResult;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ImportModule extends AbstractInsightImportModule<ImportConfiguration>
        implements InsightImportModule<ImportConfiguration> {

    private final Logger logger = LoggerFactory.getLogger(ImportModule.class);

    private final JiraAuthenticationContext authCtx;
    private final PropertyManager propertyManager;
    private final JiraLicenseManager jiraLicenseManager;
    private final JIRAService jiraService;

    public ImportModule(final JiraAuthenticationContext authCtx,
            final PropertyManager propertyManager,
            final JiraLicenseManager jiraLicenseManager,
            final JIRAService jiraService) {
        this.authCtx = authCtx;
        this.propertyManager = propertyManager;
        this.jiraLicenseManager = jiraLicenseManager;
        this.jiraService = jiraService;
    }

    @Override
    public ImportDataHolder dataHolder(ImportConfiguration configuration,
            ModuleOTSelector moduleOTSelector,
            @Nullable List<DataLocator> configuredDataLocators,
            @Nullable List<ModuleOTSelector> enabledModuleOTSelectors) throws ImportComponentException {

        try {
            List<DataEntry> dataEntries = new ArrayList<>();
            if (!moduleOTSelector.isEmpty()) {
                ContentManager contentManager =
                        new ContentManager(configuration, configuredDataLocators, enabledModuleOTSelectors);
                ImportDataValues importDataValues = contentManager.getDataEntries(moduleOTSelector);
                if (importDataValues.hasDataToImport()) {
                    for (ImportDataValue importDataValue : importDataValues.getImportDataValues()) {
                        SimpleDataEntry simpleDataEntry =
                                new SimpleDataEntry(importDataValue.getAttributes(), importDataValue.getAvatarData());
                        dataEntries.add(simpleDataEntry);
                    }
                    if (importDataValues.hasWarning()) {
                        return InMemoryDataHolder.createWithParseResult(dataEntries,
                                importDataValues.getExternalDataParseResult());
                    } else {
                        return InMemoryDataHolder.createInMemoryDataHolder(dataEntries);
                    }
                } else {
                    return InMemoryDataHolder.createNoDataToImport();
                }
            } else {
                return InMemoryDataHolder.createNoDataToImport();
            }
        } catch (Exception e) {
            if (e instanceof ImportComponentException) {
                throw e;
            }
            logger.error("Unable to fetch data holder using conf " + configuration, e);
            throw new ImportComponentException("Unable to fetch data holder from Module", e);
        }
    }

    @Override
    public ImportDataHolder dataHolder(ImportConfiguration configuration, ModuleOTSelector moduleOTSelector)
            throws ImportComponentException {
        return dataHolder(configuration, moduleOTSelector, null, null);
    }

    @Override
    public ImportConfiguration importModuleConfigurationTemplate() {
        return new ImportConfiguration();
    }

    @Override
    public InsightSchemaExternal predefinedStructure(ImportConfiguration configuration) {
        try {
            StructureManager structureManager = new StructureManager(configuration);
            return structureManager.getPredefinedStructure();
        } catch (Exception e) {
            if (e instanceof ImportComponentException) {
                throw e;
            }

            logger.error("Unable to prepare insight external data for predefined structure " + configuration, e);
            throw new ImportComponentException(e);
        }
    }

    @Override
    public TemplateImportConfiguration templateImportConfiguration(ImportConfiguration configuration) {
        try {
            StructureManager structureManager = new StructureManager(configuration);
            InsightSchemaExternal insightSchemaExternal = structureManager.getPredefinedStructure();
            List<TemplateImportConfiguration.ObjectTypeMapping> otMappings = new ArrayList<>();
            for (ObjectTypeExternal objectTypeExternal : insightSchemaExternal.getObjectSchema()
                    .getObjectTypes()) {
                otMappings.addAll(getObjectTypeMappings(objectTypeExternal));
            }
            return TemplateImportConfiguration.createConfigWithMapping(otMappings);

        } catch (Exception e) {
            logger.error("Unable to prepare insight external data for template configuration " + configuration, e);
            throw new ImportComponentException(e);
        }
    }

    private List<TemplateImportConfiguration.ObjectTypeMapping> getObjectTypeMappings(ObjectTypeExternal externalFormat) {

        List<TemplateImportConfiguration.ObjectTypeMapping> otMappings = new ArrayList<>();
        TemplateImportConfiguration.ObjectTypeMapping otMapping = mappingForExternalFormat(externalFormat);
        if (otMapping != null) {
            otMappings.add(otMapping);
        }
        if (externalFormat.getObjectTypeChildren() != null) {
            for (ObjectTypeExternal objectTypeExternal : externalFormat.getObjectTypeChildren()) {
                otMappings.addAll(getObjectTypeMappings(objectTypeExternal));
            }
        }

        return otMappings;
    }

    private TemplateImportConfiguration.ObjectTypeMapping mappingForExternalFormat(ObjectTypeExternal externalFormat) {

        ObjectTypeModuleExternal objectTypeImportExternal = (ObjectTypeModuleExternal) externalFormat;

        if (objectTypeImportExternal.isCreateOTConfiguration()) {
            List<ObjectTypeAttributeExternal> objectTypeAttributeExternals =
                    objectTypeImportExternal.getObjectTypeAttributes();
            TemplateImportConfiguration.ObjectTypeMapping otMapping =
                    new TemplateImportConfiguration.ObjectTypeMapping();

            otMapping.setObjectTypeName(objectTypeImportExternal.getName());
            otMapping.setSelector(objectTypeImportExternal.getSelector());
            otMapping.setHandleMissingObjects(objectTypeImportExternal.getHandleMissingObjects());

            otMapping.setHavingAttributeNames(objectTypeAttributeExternals.stream()
                    .map(ObjectTypeAttributeExternal::getName)
                    .collect(Collectors.toList()));

            otMapping.setDisabledByDefault(objectTypeImportExternal.getDisabledByDefault());

            List<TemplateImportConfiguration.AttributeMapping> attributeMappings = new ArrayList<>();

            for (ObjectTypeAttributeExternal objectTypeAttributeExternal : objectTypeAttributeExternals) {
                TemplateImportConfiguration.AttributeMapping attributeMapping =
                        new TemplateImportConfiguration.AttributeMapping();

                ObjectTypeAttributeModuleExternal objectTypeAttributeImportExternal =
                        (ObjectTypeAttributeModuleExternal) objectTypeAttributeExternal;

                attributeMapping.setAttributeName(objectTypeAttributeExternal.getName());
                attributeMapping.setAttributeLocators(objectTypeAttributeImportExternal.getDataLocators());

                if (objectTypeAttributeImportExternal instanceof ReferencedObjectTypeAttributeModuleExternal) {
                    attributeMapping.setObjectMappingIQL(
                            ((ReferencedObjectTypeAttributeModuleExternal) objectTypeAttributeImportExternal).getObjectMappingIQL());
                }

                attributeMapping.setExternalIdPart(objectTypeAttributeImportExternal.isIdentifier());
                attributeMappings.add(attributeMapping);
            }

            otMapping.setAttributesMapping(attributeMappings);

            return otMapping;
        }

        return null;
    }

    @Override
    public List<DataLocator> fetchDataLocators(ImportConfiguration configuration, ModuleOTSelector moduleOTSelector)
            throws ImportComponentException {
        try {
            if (moduleOTSelector.isEmpty()) {
                new ArrayList<>();
            }

            StructureManager structureManager = new StructureManager(configuration);
            return structureManager.getDataLocators(moduleOTSelector);
        } catch (Exception e) {
            if (e instanceof ImportComponentException) {
                throw e;
            }

            logger.error("Unable to fetch data locators for configuration " + configuration, e);
            throw new ImportComponentException(e);
        }
    }

    @Override
    public ImportConfiguration convertConfigurationFromJSON(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonString, ImportConfiguration.class);
        } catch (IOException ee) {
            logger.warn("Unable to transform config to ImportConfiguration " + jsonString, ee);
            throw new IllegalArgumentException("Unable to parse configuration as ImportConfiguration " + jsonString,
                    ee);
        }
    }

    @Override
    public ValidationResult validateAndTestConfiguration(ImportConfiguration configuration) {
        if (ClientProvider.INSTANCE.validClient(configuration)) {
            return ValidationResult.OK();
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("Connection", I18FactoryFake.getInstance()
                    .getProperty("rlabs.module.i18n.import.module.validation.Invalid.connection"));
            return ValidationResult.error(error);
        }
    }

    @Override
    public ValidationResult postFunction(ImportConfiguration configuration,
            int affectedObjectSchemaId,
            ApplicationUser actor) {
        CacheProvider.getInstance()
                .Reset();
        CacheProvider.getInstance()
                .Clean();
        return super.postFunction(configuration, affectedObjectSchemaId, actor);
    }
}