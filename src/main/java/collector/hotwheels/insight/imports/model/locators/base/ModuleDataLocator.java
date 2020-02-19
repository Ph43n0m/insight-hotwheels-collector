package collector.hotwheels.insight.imports.model.locators.base;

import com.riadalabs.jira.plugins.insight.services.imports.common.external.DataLocator;

public class ModuleDataLocator extends DataLocator {

    public enum LocatorType {
        JAVA_CODE_VALUE,
        JAVA_CODE_PARENT_VALUE,
        JAVA_METHOD_VALUE,
        REFERENCED_VALUE
    }

    protected LocatorType locatorType;

    public ModuleDataLocator(String locator) {
        super(locator);
    }

    public LocatorType getLocatorType() {
        return locatorType;
    }

}
