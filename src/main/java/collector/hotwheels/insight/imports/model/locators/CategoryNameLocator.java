package collector.hotwheels.insight.imports.model.locators;

import collector.hotwheels.api.model.BaseItem;
import collector.hotwheels.insight.imports.model.locators.base.JavaCodeDataLocator;

import java.util.Collections;
import java.util.List;

import static collector.hotwheels.insight.util.StringUtils.isNullOrEmpty;

public class CategoryNameLocator extends JavaCodeDataLocator<BaseItem> {

    public CategoryNameLocator(String locator) {
        super(locator);
    }

    @Override
    public List<String> getCodeLocatorValue(BaseItem obj) {
        String ret = obj.getName();

        if (!isNullOrEmpty(ret) && ret.startsWith("Hot Wheels by")) {
            ret = ret.replace("Hot Wheels by", "")
                    .trim();
        }

        return Collections.singletonList(ret);
    }

}
