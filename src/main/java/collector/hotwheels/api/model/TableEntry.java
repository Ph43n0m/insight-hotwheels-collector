package collector.hotwheels.api.model;

import collector.hotwheels.insight.util.HashProvider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static collector.hotwheels.insight.imports.model.locators.base.LocatorHelper.getPropertyNames;
import static collector.hotwheels.insight.imports.model.locators.base.LocatorHelper.tryGetValue;
import static collector.hotwheels.insight.util.StringUtils.isNullOrEmpty;

public final class TableEntry extends BaseItem {

    private String castingName;
    private String parentPageId;
    private boolean treasureHunt;
    private boolean superTreasureHunt;

    private String collectionNumber;
    private String toyNumber;
    private String modelName;
    private String realName;
    private String wheelType;
    private String country;
    private String notes;
    private String year;
    private String series;
    private String sticker;
    private String tampo;
    private String scale;
    private String origin;
    private String policeDept;
    private String quantity;

    private String color;
    private String baseColorType;
    private String windowColor;
    private String interiorColor;
    private String wheelColor;
    private String engineColor;
    private String roofColor;
    private String bladeColor;
    private String propellerColor;
    private String blimpColor;
    private String wingColor;
    private String riderColor;

    private String driver;
    private String record;
    private String image;
    private String plate;

    private Map<String, String> otherAttributes = new HashMap<>();

    public TableEntry(BaseItem baseItem) {
        this.castingName = baseItem.getName();
        this.parentPageId = baseItem.getId();
    }

    @Override
    public String getName() {
        String ret = this.castingName;

        if (!isNullOrEmpty(this.toyNumber)) {
            ret += "-" + toyNumber;
        }

        return ret;
    }

    @Override
    public String getId() {
        String ret = "someInitString";

        try {

            if (!isNullOrEmpty(this.toyNumber)) {
                ret += this.toyNumber;
            }
            if (!isNullOrEmpty(this.collectionNumber)) {
                ret += this.collectionNumber;
            }
            if (!isNullOrEmpty(this.modelName)) {
                ret += this.modelName;
            }
            if (!isNullOrEmpty(this.realName)) {
                ret += this.realName;
            }
            if (!isNullOrEmpty(this.wheelType)) {
                ret += this.wheelType;
            }
            if (!isNullOrEmpty(this.country)) {
                ret += this.country;
            }
            if (!isNullOrEmpty(this.year)) {
                ret += this.year;
            }
            if (!isNullOrEmpty(this.series)) {
                ret += this.series;
            }
            if (!isNullOrEmpty(this.record)) {
                ret += this.record;
            }
            if (!isNullOrEmpty(this.driver)) {
                ret += this.driver;
            }
            if (!isNullOrEmpty(this.tampo)) {
                ret += this.tampo;
            }
            if (!isNullOrEmpty(this.sticker)) {
                ret += this.sticker;
            }

            Set<String> objProperties = new HashSet<>();
            getPropertyNames(this.getClass(), objProperties);

            for (String field : objProperties) {
                if (field.contains("color")) {
                    Object value = tryGetValue(this, field);
                    if (value != null) {
                        ret += value;
                    }
                }
            }

            ret = HashProvider.getMD5Hash(ret);
        } catch (Exception ex) {
            throw ex;
        }

        return ret;
    }

    public String getCastingName() {
        return castingName;
    }

    public void setCastingName(String castingName) {
        this.castingName = castingName;
    }

    public String getParentPageId() {
        return parentPageId;
    }

    public boolean getTreasureHunt() {
        return treasureHunt;
    }

    public void setTreasureHunt(boolean treasureHunt) {
        this.treasureHunt = treasureHunt;
    }

    public boolean getSuperTreasureHunt() {
        return superTreasureHunt;
    }

    public void setSuperTreasureHunt(boolean superTreasureHunt) {
        this.superTreasureHunt = superTreasureHunt;
    }

    public String getCollectionNumber() {
        return collectionNumber;
    }

    public void setCollectionNumber(String collectionNumber) {
        this.collectionNumber = collectionNumber;
    }

    public String getToyNumber() {
        return toyNumber;
    }

    public void setToyNumber(String toyNumber) {
        this.toyNumber = toyNumber;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getWheelType() {
        return wheelType;
    }

    public void setWheelType(String wheelType) {
        this.wheelType = wheelType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSticker() {
        return sticker;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }

    public String getTampo() {
        return tampo;
    }

    public void setTampo(String tampo) {
        this.tampo = tampo;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getPoliceDept() {
        return policeDept;
    }

    public void setPoliceDept(String policeDept) {
        this.policeDept = policeDept;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getBaseColorType() {
        return baseColorType;
    }

    public void setBaseColorType(String baseColorType) {
        this.baseColorType = baseColorType;
    }

    public String getRiderColor() {
        return riderColor;
    }

    public void setRiderColor(String riderColor) {
        this.riderColor = riderColor;
    }

    public String getWindowColor() {
        return windowColor;
    }

    public void setWindowColor(String windowColor) {
        this.windowColor = windowColor;
    }

    public String getInteriorColor() {
        return interiorColor;
    }

    public void setInteriorColor(String interiorColor) {
        this.interiorColor = interiorColor;
    }

    public String getWheelColor() {
        return wheelColor;
    }

    public void setWheelColor(String wheelColor) {
        this.wheelColor = wheelColor;
    }

    public String getEngineColor() {
        return engineColor;
    }

    public void setEngineColor(String engineColor) {
        this.engineColor = engineColor;
    }

    public String getRoofColor() {
        return roofColor;
    }

    public void setRoofColor(String roofColor) {
        this.roofColor = roofColor;
    }

    public String getBladeColor() {
        return bladeColor;
    }

    public void setBladeColor(String bladeColor) {
        this.bladeColor = bladeColor;
    }

    public String getPropellerColor() {
        return propellerColor;
    }

    public void setPropellerColor(String propellerColor) {
        this.propellerColor = propellerColor;
    }

    public String getBlimpColor() {
        return blimpColor;
    }

    public void setBlimpColor(String blimpColor) {
        this.blimpColor = blimpColor;
    }

    public String getWingColor() {
        return wingColor;
    }

    public void setWingColor(String wingColor) {
        this.wingColor = wingColor;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public Map<String, String> getOtherAttributes() {
        return otherAttributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TableEntry tableEntry = (TableEntry) o;
        return Objects.equals(getId(), tableEntry.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
