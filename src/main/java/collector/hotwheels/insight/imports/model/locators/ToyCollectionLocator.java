package collector.hotwheels.insight.imports.model.locators;

import collector.hotwheels.api.model.TableEntry;
import collector.hotwheels.insight.imports.model.locators.base.ReferencedDataLocator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static collector.hotwheels.insight.util.StringUtils.URIDecode;
import static collector.hotwheels.insight.util.StringUtils.isNullOrEmpty;

public class ToyCollectionLocator extends ReferencedDataLocator<TableEntry, Object> {

    public ToyCollectionLocator(String locator) {
        super(locator);
    }

    @Override
    public List<String> getReferencedLocatorValue(TableEntry tableEntry, Object ignore) {
        Set<String> values = new HashSet<>();

        if (tableEntry != null && !isNullOrEmpty(tableEntry.getSeries())) {
            String rawSeriesText = tableEntry.getSeries()
                    .replace("_", " ");

            addValues(Arrays.asList(rawSeriesText.split(System.lineSeparator())), values);

        }

        return new ArrayList<>(values);
    }

    private void addValues(List<String> valuesToAdd, Set<String> resultSet) {
        for (String seriesLine : valuesToAdd) {
            if (seriesLine.contains("/")) {
                continue;
            } else if (seriesLine.contains("(") && seriesLine.contains(")")) {
                seriesLine = seriesLine.substring(0, seriesLine.indexOf("("))
                        .trim();
            }

            if (seriesLine.contains("#")) {
                seriesLine = seriesLine.split("#")[0].trim();
            }
            if (!isNullOrEmpty(seriesLine)) {
                seriesLine = seriesLine.replaceAll(":(?i)category:", "");
                seriesLine = URIDecode(seriesLine.replace("_", " "));

                resultSet.add(seriesLine.trim());

                if (seriesLine.contains("&")) {
                    List<String> andSeparated = Arrays.asList(seriesLine.split("\\s*&\\s*"));
                    addValues(andSeparated, resultSet);
                }
                if (seriesLine.contains(":")) {
                    List<String> andSeparated = Arrays.asList(seriesLine.split("\\s*:\\s*"));
                    addValues(andSeparated, resultSet);
                }
                if (seriesLine.contains("-")) {
                    List<String> andSeparated = Arrays.asList(seriesLine.split("\\s*-\\s*"));
                    addValues(andSeparated, resultSet);
                }
            }

        }
    }
}
