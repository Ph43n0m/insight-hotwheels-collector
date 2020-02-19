package collector.hotwheels.api.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public final class PageData extends BaseItem {

    private final Logger logger = LoggerFactory.getLogger(PageData.class);

    private String description;
    private String designer;
    private String image;
    private String debutSeries;
    private String producingYears;
    private String production;
    private String toyNumber;
    private String horsepower;
    private String topSpeed;
    private String engine;
    private String acceleration;
    private String born;
    private String birthplace;
    private String specialty;
    private String carDesigner;
    private boolean hasCastingData;
    private Set<Category> categories = new HashSet<>();
    private Set<TableEntry> tableEntries = new HashSet<>();
    private RevisionContent revisionContent;

    public PageData(BaseItem baseItem) {
        super(baseItem);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDesigner() {
        return designer;
    }

    public void setDesigner(String designer) {
        this.designer = designer;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDebutSeries() {
        return debutSeries;
    }

    public void setDebutSeries(String debutSeries) {
        this.debutSeries = debutSeries;
    }

    public String getProducingYears() {
        return producingYears;
    }

    public void setProducingYears(String producingYears) {
        this.producingYears = producingYears;
    }

    public String getProduction() {
        return production;
    }

    public void setProduction(String production) {
        this.production = production;
    }

    public String getToyNumber() {
        return toyNumber;
    }

    public void setToyNumber(String toyNumber) {
        this.toyNumber = toyNumber;
    }

    public String getHorsepower() {
        return horsepower;
    }

    public void setHorsepower(String horsepower) {
        this.horsepower = horsepower;
    }

    public String getTopSpeed() {
        return topSpeed;
    }

    public void setTopSpeed(String topSpeed) {
        this.topSpeed = topSpeed;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(String acceleration) {
        this.acceleration = acceleration;
    }

    public String getBorn() {
        return born;
    }

    public void setBorn(String born) {
        this.born = born;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getCarDesigner() {
        return carDesigner;
    }

    public void setCarDesigner(String carDesigner) {
        this.carDesigner = carDesigner;
    }

    public boolean getHasCastingData() {
        return hasCastingData;
    }

    public void setHasCastingData(boolean hasCastingData) {
        this.hasCastingData = hasCastingData;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Set<TableEntry> getTableEntries() {
        return tableEntries;
    }

    public void setTableEntries(Set<TableEntry> tableEntries) {
        this.tableEntries = tableEntries;
    }

    public RevisionContent getRevisionContent() {
        return revisionContent;
    }

    public void setRevisionContent(RevisionContent revisionContent) {
        this.revisionContent = revisionContent;
    }


}
