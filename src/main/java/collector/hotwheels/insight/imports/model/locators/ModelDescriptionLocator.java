package collector.hotwheels.insight.imports.model.locators;

import collector.hotwheels.api.model.PageData;
import collector.hotwheels.insight.imports.model.locators.base.JavaCodeDataLocator;

import java.util.ArrayList;
import java.util.List;

import static collector.hotwheels.insight.util.StringUtils.isNullOrEmpty;

public class ModelDescriptionLocator extends JavaCodeDataLocator<PageData> {

    public ModelDescriptionLocator(String locator) {
        super(locator);
    }

    @Override
    public List<String> getCodeLocatorValue(PageData obj) {
        List<String> ret = new ArrayList<>();

        if (obj != null && !isNullOrEmpty(obj.getDescription())) {
            ret.add(String.join("</br>", obj.getDescription()
                    .split(System.lineSeparator())));
        }
        return ret;
    }

}
