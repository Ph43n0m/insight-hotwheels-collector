package collector.hotwheels.api.model;

import collector.hotwheels.api.model.interfaces.IBase;
import com.google.common.base.Objects;

import static collector.hotwheels.insight.util.StringUtils.isNullOrEmpty;

public class BaseItem implements IBase {

    private String id;
    private String name;
    private String imageUrl;
    private String imageFileName;

    public BaseItem() {
    }

    public BaseItem(BaseItem baseItem) {
        this.id = baseItem.getId();
        this.name = baseItem.getName();
        this.imageFileName = baseItem.imageFileName;
        this.imageUrl = baseItem.imageUrl;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String getImageFileName() {
        return imageFileName;
    }

    @Override
    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    @Override
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean isValidBaseItem() {
        return !isNullOrEmpty(this.id) && !isNullOrEmpty(this.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseItem baseItem = (BaseItem) o;
        return Objects.equal(id, baseItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
