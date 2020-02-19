package collector.hotwheels.insight.imports.model.locators;

import collector.hotwheels.api.model.Category;
import collector.hotwheels.api.model.PageData;
import collector.hotwheels.insight.imports.model.locators.base.ReferencedDataLocator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static collector.hotwheels.insight.util.StringUtils.isNullOrEmpty;

public class ModelCategoriesLocator extends ReferencedDataLocator<PageData, Object> {

    public ModelCategoriesLocator(String locator) {
        super(locator);
    }

    @Override
    public List<String> getReferencedLocatorValue(PageData pageData, Object ignore) {
        Set<String> values = new HashSet<>();

        if (pageData != null && pageData.getCategories() != null && !pageData.getCategories()
                .isEmpty()) {
            for (Category category : pageData.getCategories()) {
                if (!isNullOrEmpty(category.getId())) {
                    values.add(category.getId());
                }
            }
        }

        return new ArrayList<>(values);
    }
}
