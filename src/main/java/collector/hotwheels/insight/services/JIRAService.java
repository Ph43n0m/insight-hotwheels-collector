package collector.hotwheels.insight.services;

import com.atlassian.jira.config.util.DefaultJiraHome;

import java.io.File;
import java.util.Date;

public abstract interface JIRAService {

    public static final String ISO8061 = "jira.date.time.picker.use.iso8061";

    public abstract String getBaseURL();

    public abstract String getDateFormat(Date date);

    public abstract String getDateTimeFormat(Date date);

    String JIRA_HOME_PATH = new DefaultJiraHome().getHomePath();

    String JIRA_HOME_IMPORT_INSIGHT_HW_PATH =
            JIRA_HOME_PATH + File.separator + "import" + File.separator + "insight" + File.separator + "hw";

}