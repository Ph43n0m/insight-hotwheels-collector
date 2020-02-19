package collector.hotwheels.insight.imports.model.locators.base;

import java.util.List;

public abstract class JavaCodeDataLocator<T> extends ModuleDataLocator {

    public JavaCodeDataLocator(String locator) {
        super(locator);
        super.locatorType = LocatorType.JAVA_CODE_VALUE;
    }

    public abstract List<String> getCodeLocatorValue(T dataObject);

}