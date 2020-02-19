package collector.hotwheels.insight.imports;

import com.riadalabs.jira.plugins.insight.services.model.StatusTypeBean;

import java.util.HashMap;
import java.util.Map;

public class ModuleProperties {

    public enum Status {
        FOUND("FOUND", "Found", StatusTypeBean.STATUS_CATEGORY_ACTIVE),
        NOT_FOUND("NOT_FOUND", "Not Found", StatusTypeBean.STATUS_CATEGORY_INACTIVE),

        UNKNOWN("UNKNOWN", "Unknown", StatusTypeBean.STATUS_CATEGORY_INACTIVE);

        String source;
        String name;
        int category;
        private static Map<String, Status> operatorToEnumMapping;

        private Status(String source, String name, int category) {
            this.source = source;
            this.name = name;
            this.category = category;
        }

        public static Status getInstance(String source) {
            if (operatorToEnumMapping == null) {
                initMapping();
            }
            return operatorToEnumMapping.get(source);
        }

        private static void initMapping() {
            operatorToEnumMapping = new HashMap<>();
            for (Status s : values()) {
                operatorToEnumMapping.put(s.source, s);
            }
        }

        public String getName() {
            return name;
        }

        public int getCategory() {
            return category;
        }
    }

    // Reference Names
    public static final String REF_NAME_USING = "using";
    public static final String REF_NAME_ATTACHED = "attached";
    public static final String REF_NAME_CLONES = "clones";
    public static final String REF_NAME_BASED = "based";
    public static final String REF_NAME_INSTALLED = "installed";
    public static final String REF_NAME_CONTAINS = "contains";
    public static final String REF_NAME_BELONGS = "belongs";
    public static final String REF_NAME_CONNECTED = "connected";

    // ROOT Structure Items
    public static final String HOT_WHEELS_ROOT = "Hot Wheels";

    public static final String NAME = "Name";
    public static final String DESCRIPTION = "Description";

    public static final String STATUS = "Status";

    public static final String ID = "Id";

    public static final String CATEGORY = "Category";
    public static final String CATEGORIES = "Categories";

    public static final String MODEL = "Model";
    public static final String MODELS = "Models";
    public static final String DESIGNER = "Designer";
    public static final String DEBUT_SERIES = "Debut Series";
    public static final String PRODUCING_YEARS = "Producing Years";
    public static final String TOY_NUMBER = "Toy Number";
    public static final String PRODUCTION = "Production";
    public static final String HORSEPOWER = "Horsepower";
    public static final String TOP_SPEED = "Top Speed";
    public static final String ENGINE = "Engine";
    public static final String ACCELERATION = "Acceleration";
    public static final String BORN = "Born";
    public static final String BIRTHPLACE = "Birthplace";
    public static final String SPECIALTY = "Specialty";
    public static final String CAR_DESIGNER = "Car Designer";

    public static final String TOY = "Toy";
    public static final String CASTING = "Casting";
    public static final String TREASURE_HUNT = "Treasure Hunt";
    public static final String SUPER_TREASURE_HUNT = "Super Treasure Hunt";
    public static final String COLLECTION_NUMBER = "Collection Number";
    public static final String REAL_NAME = "Real Name";
    public static final String YEAR = "Year";
    public static final String SERIES = "Series";
    public static final String SCALE = "Scale";
    public static final String WHEEL_TYPE = "Wheel Type";
    public static final String COUNTRY = "Country";
    public static final String NOTES = "Notes";
    public static final String STICKER = "Sticker";
    public static final String TAMPO = "Tampo";
    public static final String ORIGIN = "Origin";
    public static final String POLICE_DEPARTMENT = "Police Department";
    public static final String QUANTITY = "Quantity";
    public static final String COLOR = "Color";
    public static final String BASE_COLOR_TYPE = "Base Color Type";
    public static final String WINDOW_COLOR = "Window Color";
    public static final String INTERIOR_COLOR = "Interior Color";
    public static final String WHEEL_COLOR = "Wheel Color";
    public static final String ENGINE_COLOR = "Engine Color";
    public static final String ROOF_COLOR = "Roof Color";
    public static final String BLADE_COLOR = "Blade Color";
    public static final String PROPELLER_COLOR = "Propeller Color";
    public static final String BLIMP_COLOR = "Blimp Color";
    public static final String WING_COLOR = "Wing Color";
    public static final String RIDER_COLOR = "Rider Color";
    public static final String DRIVER = "Driver";
    public static final String RECORD = "Record";
    public static final String PLATE = "Plate";
    public static final String IMAGE = "Image";
    public static final String OTHER_ATTRIBUTES = "Other Attributes";

    public static final String COLLECTION = "Collection";
    public static final String COLLECTIONS = "Collections";

}
