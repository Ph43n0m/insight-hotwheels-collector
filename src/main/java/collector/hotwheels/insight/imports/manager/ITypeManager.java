package collector.hotwheels.insight.imports.manager;

import com.riadalabs.jira.plugins.insight.services.imports.common.external.DataLocator;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.ImportDataValues;

import java.util.List;

public interface ITypeManager {

    List<DataLocator> getDataLocators();

    ImportDataValues getDataHolder();
}
