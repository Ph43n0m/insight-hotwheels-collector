package collector.hotwheels.insight.imports.model.locators;

import collector.hotwheels.api.model.PageData;
import collector.hotwheels.api.model.TableEntry;
import collector.hotwheels.insight.imports.model.locators.base.ReferencedDataLocator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static collector.hotwheels.insight.util.StringUtils.isNullOrEmpty;

public class CollectionModelsLocator extends ReferencedDataLocator<PageData, Object> {

    public CollectionModelsLocator(String locator) {
        super(locator);
    }

    @Override
    public List<String> getReferencedLocatorValue(PageData pageData, Object ignore) {
        Set<String> values = new HashSet<>();

        if (pageData != null && pageData.getTableEntries() != null && !pageData.getTableEntries()
                .isEmpty()) {
            for (TableEntry tableEntry : pageData.getTableEntries()) {
                if (!isNullOrEmpty(tableEntry.getModelName())) {
                    values.add(tableEntry.getModelName());
                }
            }
        }

        return new ArrayList<>(values);
    }
}
