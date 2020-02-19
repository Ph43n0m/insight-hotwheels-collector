package collector.hotwheels.api.client;

import collector.hotwheels.api.model.BaseItem;
import collector.hotwheels.api.model.RevisionContent;
import collector.hotwheels.api.model.TableEntry;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static collector.hotwheels.insight.util.StringUtils.isNullOrEmpty;

public final class WikiaContentParser {

    private static final Logger logger = LoggerFactory.getLogger(WikiaContentParser.class);

    private static final String ROW_SEPARATOR_REGEX = "\\|";
    private static final String ROW_SEPARATOR = "|";
    private static final String VALUE_SEPARATOR = "=";
    private static final String VALUE_SEPARATOR_2 = ":";
    private static final String CASTING_START_REGEX = "\\{\\{(?i)Casting";
    private static final String CASTING_END = "}}";
    private static final String DESCRIPTION_REGEX = "==(.*)(?i)Description(.*?)==";
    private static final String SPECIFICATIONS_REGEX = "==(.*)(?i)Specification(.*?)==";
    private static final String CARD_REGEX = "==(.*)(?i)Card(.*?)==";
    private static final String VEHICLE_REGEX = "==(.*)(?i)Vehicle(.*?)==";
    private static final String SCALE_REGEX = "==(.*)(?i)Scale(.*?)==";
    private static final String SPECIFICATIONS_END_REGEX = "\n\\[\\[";
    private static final String DOUBLE_EQ = "==";

    private static final String NEW_LINE = "\n";
    private static final String DOUBLE_NEW_LINE = "\n\n";
    private static final String IMAGE_TAG_REGEX = "(?i)Image:";
    private static final String FILE_TAG_REGEX = "(?i)File:";
    private static final String IMAGE_NA_REGEX = "(?i)image not available.jpg";
    private static final String IMAGE_NA_2_REGEX = "(?i)image_not_available.jpg";

    private static final String LINK_PATTERN = "(\\].*?\\]\\])" + "|" + "(\\[.*?\\]\\])";
    private static final String LINK_START_SINGLE = "\\[";
    private static final String LINK_END_SINGLE = "]";

    private static final String TABLE_CONTENT_START = "{|";
    private static final String TABLE_CONTENT_END = "|}";
    private static final String TABLE_ROW_START = "|-";
    private static final String TABLE_ROW_START_REGEX = "\\|-";
    private static final String TABLE_CONTENT_REGEX = "(?s)(\\{\\|(.*?)\\|\\})";
    private static final String TABLE_COL_VALUE_REGEX = "(\\||!)(.*?)\\n";
    private static final String TABLE_HEADER_COL_SEPARATOR_REGEX = "(\\||!)";
    private static final String TABLE_ROW_COL_SEPARATOR_REGEX = "\\n(\\||!)";

    private static final String HTML_NODE_REGEX = "(?i)<(.*?)>";
    private static final String DEFAULT_SORT = "\\{\\{(?i)defaultsort";
    private static final String SUPER_TREASURE_HUNT_REGEX = "(?i)[s$]uper(.*)treasure(.*)hunt";
    private static final String TREASURE_HUNT_REGEX = "(?i)trea[s$]ure(.*)hunt";
    private static final String HTML_NEWLINE_REGEX = "(?i)(<br />)|(<br/>)";
    private static final String URL_IMAGE_FILENAME_REGEX = "(.jpg|.png)";

    public static Map<String, String> GetCastingValues(RevisionContent revisionContent) {
        Map<String, String> ret = new HashMap<>();

        try {
            if (revisionContent != null && !isNullOrEmpty(revisionContent.getContent())) {
                int index = getMatch_ending_index(revisionContent.getContent(), CASTING_START_REGEX);
                if (index > 0) {
                    String rawCasting = revisionContent.getContent()
                            .substring(index)
                            .trim();

                    if (rawCasting.contains(CASTING_END)) {
                        rawCasting = rawCasting.substring(0, rawCasting.indexOf(CASTING_END));

                        if (!isNullOrEmpty(rawCasting)) {
                            ret = getValuesFromText(rawCasting, ROW_SEPARATOR_REGEX, VALUE_SEPARATOR);
                        }
                    }
                } else {
                    logger.debug("No casting data for: " + revisionContent.getPageid());
                }
            }
        } catch (Exception ex) {
            logger.error("Error getting casting values for: " + revisionContent.getPageid(), ex);
        }
        return ret;
    }

    public static Map<String, String> GetSpecificationValues(RevisionContent revisionContent) {
        Map<String, String> ret = new HashMap<>();

        try {
            if (revisionContent != null && !isNullOrEmpty(revisionContent.getContent())) {
                int index = getMatch_ending_index(revisionContent.getContent(), SPECIFICATIONS_REGEX);
                if (index > 0) {
                    String rawSpec = revisionContent.getContent()
                            .substring(index)
                            .trim();
                    if (rawSpec.contains(DOUBLE_EQ)) {
                        rawSpec = rawSpec.substring(0, rawSpec.indexOf(DOUBLE_EQ));
                    } else if (getMatch_ending_index(rawSpec, SPECIFICATIONS_END_REGEX) > 0) {
                        rawSpec = rawSpec.substring(0, getMatch_ending_index(rawSpec, SPECIFICATIONS_END_REGEX));
                    }
                    if (getMatch_ending_index(rawSpec, DEFAULT_SORT) > 0) {
                        rawSpec = rawSpec.substring(0, getMatch_ending_index(rawSpec, DEFAULT_SORT));
                    }

                    if (!isNullOrEmpty(rawSpec)) {
                        ret = getValuesFromText(rawSpec, NEW_LINE, VALUE_SEPARATOR_2);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Error getting specification values for: " + revisionContent.getPageid(), ex);
        }
        return ret;
    }

    public static Map<String, String> GetCardValues(RevisionContent revisionContent) {
        Map<String, String> ret = new HashMap<>();

        try {
            if (revisionContent != null && !isNullOrEmpty(revisionContent.getContent())) {
                int index = getMatch_ending_index(revisionContent.getContent(), CARD_REGEX);
                if (index > 0) {
                    String rawSpec = revisionContent.getContent()
                            .substring(index)
                            .trim();

                    List<String> tableContents = getTableContents(rawSpec);

                    if (!tableContents.isEmpty()) {
                        for (String tableContent : tableContents) {
                            List<String> tableRows = getTableRows(tableContent);

                            if (!tableRows.isEmpty()) {
                                for (String tableRow : tableRows) {
                                    List<String> tableRowValues = getTableRowValues(tableRow);
                                    if (!tableRowValues.isEmpty() && tableRowValues.size() == 2) {
                                        ret.put(tableRowValues.get(0), tableRowValues.get(1));
                                    }
                                }
                            }
                        }
                    } else {

                        if (rawSpec.contains(DOUBLE_EQ)) {
                            rawSpec = rawSpec.substring(0, rawSpec.indexOf(DOUBLE_EQ));
                        } else if (getMatch_ending_index(rawSpec, SPECIFICATIONS_END_REGEX) > 0) {
                            rawSpec = rawSpec.substring(0, getMatch_ending_index(rawSpec, SPECIFICATIONS_END_REGEX));
                        }
                        if (getMatch_ending_index(rawSpec, DEFAULT_SORT) > 0) {
                            rawSpec = rawSpec.substring(0, getMatch_ending_index(rawSpec, DEFAULT_SORT));
                        }

                        if (!isNullOrEmpty(rawSpec)) {
                            ret = getValuesFromText(rawSpec, NEW_LINE, VALUE_SEPARATOR_2);

                            if (ret.get("designer") != null) {
                                ret.put("cardesigner", ret.remove("designer"));
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Error getting specification values for: " + revisionContent.getPageid(), ex);
        }
        return ret;
    }

    private static Map<String, String> getValuesFromText(String text, String rowSeparator, String valueSeparator) {
        Map<String, String> ret = new HashMap<>();

        try {
            if (!isNullOrEmpty(text)) {
                String[] rows = cleanWikiFormatting(text).split(rowSeparator);

                if (rows.length > 0) {
                    for (int i = 0; i < rows.length; i++) {
                        if (!isNullOrEmpty(rows[i]) && rows[i].contains(valueSeparator)) {
                            String[] values = rows[i].split(valueSeparator);

                            if (values.length == 2 && !isNullOrEmpty(values[0].trim()) &&
                                    !isNullOrEmpty(values[1].trim())) {
                                String key = cleanWikiFormatting(values[0].toLowerCase()
                                        .trim());
                                String value = cleanWikiFormatting(values[1].trim());
                                ret.put(key, value);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Error getting values from: " + text, ex);
        }
        return ret;
    }

    public static String GetDescription(RevisionContent revisionContent) {
        String ret = "";

        try {
            if (revisionContent != null && !isNullOrEmpty(revisionContent.getContent())) {
                String description;
                int index = getMatch_ending_index(revisionContent.getContent(), DESCRIPTION_REGEX);
                if (index > 0) {
                    description = revisionContent.getContent()
                            .substring(index)
                            .trim();

                    if (description.contains(DOUBLE_EQ)) {
                        description = description.substring(0, description.indexOf(DOUBLE_EQ));
                    } else if (description.contains(DOUBLE_NEW_LINE)) {
                        description = description.substring(0, description.indexOf(DOUBLE_NEW_LINE));
                    }

                    if (!isNullOrEmpty(description)) {
                        ret = cleanWikiFormatting(description);
                    }
                } else {
                    index = getMatch_starting_index(revisionContent.getContent(), VEHICLE_REGEX);
                    if (index > 0) {
                        ret = getCleanDescriptionText(revisionContent.getContent(), 0, index);
                    } else {
                        index = getMatch_starting_index(revisionContent.getContent(), SCALE_REGEX);
                        if (index > 0) {
                            ret = getCleanDescriptionText(revisionContent.getContent(), 0, index);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Error getting description for: " + revisionContent.getPageid(), ex);
        }
        return ret;
    }

    private static String getCleanDescriptionText(String text, int from, int to) {
        String ret = "";
        try {
            if (!isNullOrEmpty(text)) {
                ret = text.substring(from, to)
                        .trim();
                if (!isNullOrEmpty(ret)) {
                    ret = cleanWikiFormatting(ret);
                    if (!ret.contains(System.lineSeparator())) {
                        ret = "";
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Error getting content.", ex);
        }
        return ret;
    }

    public static Set<TableEntry> GetTableEntries(BaseItem baseItem, RevisionContent revisionContent) {
        Set<TableEntry> ret = new HashSet<>();

        try {
            if (revisionContent != null && !isNullOrEmpty(revisionContent.getContent())) {

                List<String> tableContents = getTableContents(revisionContent.getContent());

                for (String tableContent : tableContents) {

                    List<String> tableRows = getTableRows(tableContent);
                    //TODO: respect rowspans....
                    if (!tableRows.isEmpty()) {
                        List<String> headerValues = new ArrayList<>();

                        for (String tableRow : tableRows) {
                            if (headerValues.isEmpty()) {
                                headerValues = getTableHeaderValues(tableRow);
                            } else {
                                List<String> tableRowValues = getTableRowValues(tableRow);
                                if (!tableRowValues.isEmpty() && tableRowValues.size() > 1) {
                                    ret.addAll(getTableEntries_FromValueRows(baseItem, headerValues, tableRowValues));
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Error getting versions for: " + revisionContent.getPageid(), ex);
        }
        return ret;
    }

    private static List<String> getTableRows(String tableContent) {
        List<String> ret = new ArrayList<>();

        try {
            if (!isNullOrEmpty(tableContent) && tableContent.contains(TABLE_ROW_START)) {
                String[] rows = tableContent.split(TABLE_ROW_START_REGEX);
                if (rows.length > 0) {
                    ret = Arrays.asList(rows);
                }
            }
        } catch (Exception ex) {
            logger.error("Error getting table rows.", ex);
        }
        return ret;
    }

    private static List<String> getTableContents(String content) {
        List<String> ret = new ArrayList<>();

        try {
            if (!isNullOrEmpty(content)) {
                Pattern pattern = Pattern.compile(TABLE_CONTENT_REGEX);
                Matcher matcher = pattern.matcher(content);

                while (matcher.find()) {
                    ret.add(matcher.group()
                            .replace(TABLE_CONTENT_START, "")
                            .replace(TABLE_CONTENT_END, ""));
                }
            }
        } catch (Exception ex) {
            logger.error("Error getting table content.", ex);
        }
        return ret;
    }

    public static List<String> getTableHeaderValues(String rowContent) {
        List<String> ret = new ArrayList<>();

        try {
            if (!isNullOrEmpty(rowContent)) {
                Pattern pattern = Pattern.compile(TABLE_COL_VALUE_REGEX);
                Matcher matcher = pattern.matcher(rowContent);

                while (matcher.find()) {
                    String colStr = cleanWikiFormatting(matcher.group()
                            .substring(1));

                    if (colStr.trim()
                            .equals("+ ")) {
                        continue;
                    }

                    int colSeparatorIndex = getMatch_ending_index(colStr, TABLE_HEADER_COL_SEPARATOR_REGEX);
                    if (colSeparatorIndex > 0) {
                        colStr = colStr.substring(colSeparatorIndex);
                    }
                    if (!isNullOrEmpty(colStr)) {
                        ret.add(colStr.trim());
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Error getting row values from: " + rowContent, ex);
        }
        return ret;
    }

    public static List<String> getTableRowValues(String rowContent) {
        List<String> ret = new ArrayList<>();

        try {
            if (!isNullOrEmpty(rowContent)) {
                String[] cols = rowContent.split(TABLE_ROW_COL_SEPARATOR_REGEX);
                for (int i = 1; i < cols.length; i++) {
                    ret.add(cleanWikiFormatting(cols[i]));
                }
            }
        } catch (Exception ex) {
            logger.error("Error getting row values from: " + rowContent, ex);
        }
        return ret;
    }

    private static Set<TableEntry> getTableEntries_FromValueRows(BaseItem baseItem,
            List<String> headerValues,
            List<String> rowValues) {
        Set<TableEntry> ret = new HashSet<>();

        try {
            if (headerValues != null && !headerValues.isEmpty() && rowValues != null && !rowValues.isEmpty()) {
                TableEntry tableEntry = new TableEntry(baseItem);

                for (int i = 0; i < headerValues.size(); i++) {
                    if (i < rowValues.size() && !isNullOrEmpty(headerValues.get(i)) &&
                            !isNullOrEmpty(rowValues.get(i))) {
                        if (headerValues.get(i)
                                .toLowerCase()
                                .contains("col") && headerValues.get(i)
                                .toLowerCase()
                                .contains("#")) {
                            tableEntry.setCollectionNumber(rowValues.get(i)
                                    .trim());
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("seq") && headerValues.get(i)
                                .toLowerCase()
                                .contains("#")) {
                            tableEntry.setCollectionNumber(rowValues.get(i)
                                    .trim());
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("casting")) {
                            tableEntry.setModelName(rowValues.get(i)
                                    .trim());
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("year")) {
                            tableEntry.setYear(rowValues.get(i)
                                    .trim());
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("series")) {
                            tableEntry.setSeries(rowValues.get(i)
                                    .trim());
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("tampo")) {
                            tableEntry.setTampo(rowValues.get(i)
                                    .trim());
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("#") && (headerValues.get(i)
                                .toLowerCase()
                                .contains("toy") || headerValues.get(i)
                                .toLowerCase()
                                .contains("cast"))) {
                            tableEntry.setToyNumber(rowValues.get(i)
                                    .trim());
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("color") && (headerValues.get(i)
                                .toLowerCase()
                                .contains("base") || headerValues.get(i)
                                .toLowerCase()
                                .contains("body"))) {
                            tableEntry.setBaseColorType(rowValues.get(i)
                                    .trim());
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("color") && (headerValues.get(i)
                                .toLowerCase()
                                .contains("upper") && headerValues.get(i)
                                .toLowerCase()
                                .contains("lower"))) {
                            tableEntry.setColor(rowValues.get(i)
                                    .trim());
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("color") && (headerValues.get(i)
                                .toLowerCase()
                                .contains("cab") && headerValues.get(i)
                                .toLowerCase()
                                .contains("tank"))) {
                            tableEntry.setColor(rowValues.get(i)
                                    .trim());
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("color") && (headerValues.get(i)
                                .toLowerCase()
                                .contains("upper") && headerValues.get(i)
                                .toLowerCase()
                                .contains("middle"))) {
                            tableEntry.setColor(rowValues.get(i)
                                    .trim());
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("notes")) {
                            tableEntry.setNotes(rowValues.get(i)
                                    .trim());
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("photo")) {
                            if (rowValues.get(i)
                                    .contains(".") && isNullOrEmpty(tableEntry.getImage())) {
                                tableEntry.setImage(rowValues.get(i)
                                        .trim());
                            } else {
                                String sdfsdf = "";
                            }
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("color") && headerValues.get(i)
                                .toLowerCase()
                                .contains("engine")) {
                            tableEntry.setEngineColor(rowValues.get(i)
                                    .trim());
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("base") && headerValues.get(i)
                                .toLowerCase()
                                .contains("type")) {
                            tableEntry.setBaseColorType(rowValues.get(i)
                                    .trim());
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("window")) {
                            tableEntry.setWindowColor(rowValues.get(i)
                                    .trim());
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("color") && headerValues.get(i)
                                .toLowerCase()
                                .contains("rider")) {
                            tableEntry.setRiderColor(rowValues.get(i)
                                    .trim());
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("color") && headerValues.get(i)
                                .toLowerCase()
                                .contains("wing")) {
                            tableEntry.setWingColor(rowValues.get(i)
                                    .trim());
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("chassis") && headerValues.get(i)
                                .toLowerCase()
                                .contains("color")) {
                            tableEntry.setBaseColorType(rowValues.get(i)
                                    .trim());
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("propeller")) {
                            tableEntry.setPropellerColor(rowValues.get(i)
                                    .trim());
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("blimp")) {
                            tableEntry.setBlimpColor(rowValues.get(i)
                                    .trim());
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("interior") || headerValues.get(i)
                                .toLowerCase()
                                .startsWith("int")) {
                            tableEntry.setInteriorColor(rowValues.get(i)
                                    .trim());
                        } else if (headerValues.get(i)
                                .toLowerCase()
                                .contains("blade")) {
                            tableEntry.setBladeColor(rowValues.get(i)
                                    .trim());
                        } else {
                            switch (headerValues.get(i)
                                    .trim()
                                    .toLowerCase()) {
                                case "number":
                                case "coll #":
                                case "pack":
                                case "col #":
                                case "co#":
                                case "ser #":
                                case "#":
                                case "first edition #":
                                    tableEntry.setCollectionNumber(rowValues.get(i)
                                            .trim());
                                    break;
                                case "casting name":
                                case "name":
                                case "series":
                                case "# in series":
                                case "debut series":
                                    tableEntry.setSeries(rowValues.get(i)
                                            .trim());
                                    break;
                                case "color":
                                case "leg color":
                                case "color (car)":
                                case "color (camper)":
                                case "color / chassis":
                                case "chassis color / type":
                                    tableEntry.setColor(rowValues.get(i)
                                            .trim());
                                    break;
                                case "color / tampo":
                                case "deco tampo":
                                case "tampo":
                                case "tampos":
                                case "graphics":
                                    tableEntry.setTampo(rowValues.get(i)
                                            .trim());
                                    break;
                                case "base color / types":
                                case "base color / type":
                                case "base color":
                                case "base color/type":
                                case "color / type":
                                case "body color":
                                case "base type":
                                case "base":
                                    tableEntry.setBaseColorType(rowValues.get(i)
                                            .trim());
                                    break;
                                case "top color":
                                case "canopy color":
                                case "roof color":
                                case "clip color":
                                case "roll cage color":
                                    tableEntry.setRoofColor(rowValues.get(i)
                                            .trim());
                                    break;
                                case "wheel type":
                                    tableEntry.setWheelType(rowValues.get(i)
                                            .trim());
                                    break;
                                case "wheel color":
                                    tableEntry.setWheelColor(rowValues.get(i)
                                            .trim());
                                    break;
                                case "real name":
                                    tableEntry.setRealName(rowValues.get(i)
                                            .trim());
                                    break;
                                case "toy#":
                                case "toy #":
                                case "cast #":
                                case "cast#":
                                case "toy number":
                                case "car #":
                                    tableEntry.setToyNumber(rowValues.get(i)
                                            .trim());
                                    break;
                                case "origin":
                                    tableEntry.setOrigin(rowValues.get(i)
                                            .trim());
                                    break;
                                case "police dept":
                                    tableEntry.setPoliceDept(rowValues.get(i)
                                            .trim());
                                    break;
                                case "country":
                                    tableEntry.setCountry(rowValues.get(i)
                                            .trim());
                                    break;
                                case "note":
                                case "notes":
                                case "notes ":
                                case "notes / variations":
                                case "notes / variation":
                                case "versions":
                                case "description":
                                case "subset":
                                    tableEntry.setNotes(rowValues.get(i)
                                            .trim());
                                    break;
                                case "image":
                                case "photo":
                                case "pictures":
                                case "''":
                                    if (rowValues.get(i)
                                            .contains(".") && isNullOrEmpty(tableEntry.getImage())) {
                                        tableEntry.setImage(rowValues.get(i)
                                                .trim());
                                    } else {
                                        String sfds = "";
                                    }
                                    break;
                                case "sticker":
                                case "stickers":
                                    tableEntry.setSticker(rowValues.get(i)
                                            .trim());
                                    break;
                                case "plate":
                                    tableEntry.setPlate(rowValues.get(i)
                                            .trim());
                                    break;
                                case "model name":
                                case "car name":
                                case "vehicle name":
                                case "vehicle":
                                    tableEntry.setModelName(rowValues.get(i)
                                            .trim());
                                    break;
                                case "driver":
                                    tableEntry.setDriver(rowValues.get(i)
                                            .trim());
                                    break;
                                case "record":
                                    tableEntry.setRecord(rowValues.get(i)
                                            .trim());
                                    break;
                                case "scale":
                                    tableEntry.setScale(rowValues.get(i)
                                            .trim());
                                    break;
                                case "quantity":
                                    tableEntry.setQuantity(rowValues.get(i)
                                            .trim());
                                    break;
                                default:
                                    if (!headerValues.get(i)
                                            .toLowerCase()
                                            .contains(".jpg") && !rowValues.get(i)
                                            .toLowerCase()
                                            .contains(".jpg")) {
                                        tableEntry.getOtherAttributes()
                                                .put(headerValues.get(i), rowValues.get(i));
                                    }
                                    break;
                            }
                        }
                    }
                }
                if (!tableEntry.getId()
                        .equals((new TableEntry(new BaseItem())).getId())) {

                    tableEntry.setSuperTreasureHunt(
                            getMatch_ending_index(tableEntry.getSeries() + tableEntry.getNotes(),
                                    SUPER_TREASURE_HUNT_REGEX) > 0);

                    tableEntry.setTreasureHunt(
                            getMatch_ending_index(tableEntry.getSeries() + tableEntry.getNotes(), TREASURE_HUNT_REGEX) >
                                    0);

                    ret.add(tableEntry);
                }
            }
        } catch (Exception ex) {
            logger.error("Error getting table entries.", ex);
        }
        return ret;
    }

    private static final String[] wiki_replacement_stuff = {"'''", "''"};
    private static final String STRIPPED_CHARS = " \t\u00A0\u1680\u180e\u2000\u200a\u202f\u205f\u3000\u200E";

    public static String cleanWikiFormatting(String text) {
        String ret = text;

        try {

            ret = ret.replace("[''[", "[[");
            ret = ret.replaceAll(HTML_NEWLINE_REGEX, System.lineSeparator());

            for (int i = 0; i < wiki_replacement_stuff.length; i++) {
                ret = ret.replaceAll(wiki_replacement_stuff[i], "");
            }

            while (TryGetFirstLink(ret) != null) {
                String link = TryGetFirstLink(ret);

                if (!isNullOrEmpty(link)) {
                    String linkValue = getLinkValue(link);

                    if (!isNullOrEmpty(linkValue)) {
                        ret = ret.replace(link, linkValue + System.lineSeparator());
                    }
                }
            }

            if (getMatch_ending_index(ret, IMAGE_TAG_REGEX) > 0) {
                ret = ret.replaceAll(IMAGE_TAG_REGEX, "")
                        .trim();
            }
            if (getMatch_ending_index(ret, FILE_TAG_REGEX) > 0) {
                ret = ret.replaceAll(FILE_TAG_REGEX, "")
                        .trim();
            }
            if (getMatch_ending_index(ret, IMAGE_NA_REGEX) > 0) {
                ret = ret.replaceAll(IMAGE_NA_REGEX, "")
                        .trim();
            }
            if (getMatch_ending_index(ret, IMAGE_NA_2_REGEX) > 0) {
                ret = ret.replaceAll(IMAGE_NA_2_REGEX, "")
                        .trim();
            }

            ret = StringUtils.strip(ret, STRIPPED_CHARS);

            ret = ret.replaceAll(HTML_NODE_REGEX, "");

        } catch (Exception ex) {
            logger.error("Error cleaning wiki format text.", ex);
        }
        return ret;
    }

    private static String getLinkValue(String link) {
        String ret = null;
        try {
            if (link.contains(ROW_SEPARATOR)) {
                ret = link.split(ROW_SEPARATOR_REGEX)[0];
            } else {
                ret = link;
            }

            ret = ret.replaceAll(LINK_END_SINGLE, "");
            ret = ret.replaceAll(LINK_START_SINGLE, "");

        } catch (Exception ex) {
            logger.error("Error getting link value for: " + link, ex);
        }
        return ret;
    }

    public static String TryGetFirstLink(String text) {
        String link = null;
        try {
            Matcher matcher = getRegExMatcher(text, LINK_PATTERN, Pattern.DOTALL);
            if (matcher != null) {
                link = matcher.group(0);
            }
        } catch (Exception ex) {
            logger.error("Error getting link.", ex);
        }
        return link;
    }

    public static String GetImageFilename_fromUrl(String url) {
        String ret = null;

        try {
            if (!isNullOrEmpty(url)) {
                int endIndex = getMatch_ending_index(url, URL_IMAGE_FILENAME_REGEX);
                if (endIndex > 0) {
                    url = url.substring(0, endIndex);

                    int startIndex = url.lastIndexOf("/");

                    if (startIndex > 0) {
                        ret = url.substring(startIndex + 1);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Error getting image filename from: " + url, ex);
        }

        return ret;
    }

    private static int getMatch_ending_index(String text, String regex) {
        int ret = -1;

        try {
            Matcher matcher = getRegExMatcher(text, regex);
            if (matcher != null) {
                ret = matcher.end();
            }
        } catch (Exception ex) {
            logger.error("Error getting match ending index for: " + regex, ex);
        }
        return ret;
    }

    private static int getMatch_starting_index(String text, String regex) {
        int ret = -1;

        try {
            Matcher matcher = getRegExMatcher(text, regex);
            if (matcher != null) {
                ret = matcher.start();
            }
        } catch (Exception ex) {
            logger.error("Error getting match ending index for: " + regex, ex);
        }
        return ret;
    }

    private static Matcher getRegExMatcher(String text, String regex) {
        return getRegExMatcher(text, regex, Pattern.CASE_INSENSITIVE);
    }

    private static Matcher getRegExMatcher(String text, String regex, int flags) {
        Pattern pattern = Pattern.compile(regex, flags);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher;
        } else {
            return null;
        }
    }
}
