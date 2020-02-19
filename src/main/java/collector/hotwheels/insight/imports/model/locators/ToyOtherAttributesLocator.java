package collector.hotwheels.insight.imports.model.locators;

import collector.hotwheels.api.model.TableEntry;
import collector.hotwheels.insight.imports.model.locators.base.JavaCodeDataLocator;

import java.util.ArrayList;
import java.util.List;

public class ToyOtherAttributesLocator extends JavaCodeDataLocator<TableEntry> {

    public ToyOtherAttributesLocator(String locator) {
        super(locator);
    }

    @Override
    public List<String> getCodeLocatorValue(TableEntry obj) {
        List<String> ret = new ArrayList<>();

        if (obj != null && obj.getOtherAttributes() != null && !obj.getOtherAttributes()
                .isEmpty()) {
            obj.getOtherAttributes()
                    .forEach((k, v) -> {
                        ret.add(k + ": " + v + "<br/>");
                    });

        }
        return ret;
    }

}
