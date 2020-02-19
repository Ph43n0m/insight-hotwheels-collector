package collector.hotwheels.insight.imports.model.locators.base;

import java.util.List;

public abstract class JavaCodeParentDataLocator<T, P> extends ModuleDataLocator {

    public JavaCodeParentDataLocator(String locator) {
        super(locator);
        super.locatorType = LocatorType.JAVA_CODE_PARENT_VALUE;
    }

    public abstract List<String> getCodeLocatorValue(T discoveryDataObject, P discoveryParentDataObject);

}
