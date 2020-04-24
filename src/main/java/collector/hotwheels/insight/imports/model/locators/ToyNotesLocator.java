package collector.hotwheels.insight.imports.model.locators;

import collector.hotwheels.api.model.TableEntry;
import collector.hotwheels.insight.imports.model.locators.base.JavaCodeDataLocator;

import java.util.ArrayList;
import java.util.List;

import static collector.hotwheels.insight.util.StringUtils.isNullOrEmpty;

public class ToyNotesLocator extends JavaCodeDataLocator<TableEntry> {

    public ToyNotesLocator(String locator) {
        super(locator);
    }

    @Override
    public List<String> getCodeLocatorValue(TableEntry obj) {
        List<String> ret = new ArrayList<>();

        if (obj != null && !isNullOrEmpty(obj.getNotes())) {
            ret.add(String.join("<br />", obj.getNotes()
                    .split(System.lineSeparator())));
        }
        return ret;
    }

}
