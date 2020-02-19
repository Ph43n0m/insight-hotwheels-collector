package collector.hotwheels.insight.imports.model.locators;

import collector.hotwheels.api.model.BaseItem;
import collector.hotwheels.insight.imports.ModuleProperties;
import collector.hotwheels.insight.imports.model.locators.base.JavaCodeDataLocator;

import java.util.Arrays;
import java.util.List;

public class StatusLocator extends JavaCodeDataLocator<BaseItem> {

    public StatusLocator(String locator) {
        super(locator);
    }

    @Override
    public List<String> getCodeLocatorValue(BaseItem obj) {
        return Arrays.asList(ModuleProperties.Status.FOUND.getName());
    }

}
