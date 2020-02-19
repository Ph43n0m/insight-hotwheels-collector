package collector.hotwheels.api.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public final class Category extends BaseItem {

    private final Logger logger = LoggerFactory.getLogger(Category.class);

    private Set<BaseItem> categoryMembers = new HashSet<>();

    public Category(BaseItem baseItem) {
        super(baseItem);
    }

    public Set<BaseItem> getCategoryMembers() {
        return categoryMembers;
    }

}
