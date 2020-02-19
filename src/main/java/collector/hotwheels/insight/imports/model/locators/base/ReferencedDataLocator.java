package collector.hotwheels.insight.imports.model.locators.base;

import java.util.List;

public abstract class ReferencedDataLocator<T, V> extends ModuleDataLocator {

    public ReferencedDataLocator(String locator) {
        super(locator);
        super.locatorType = LocatorType.REFERENCED_VALUE;
    }

    public abstract List<String> getReferencedLocatorValue(T dataObject, V objectDatas);

}