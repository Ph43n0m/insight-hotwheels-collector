package collector.hotwheels.insight.services;

import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class JIRAServiceImpl implements JIRAService {

    private final ApplicationProperties applicationProperties;

    @Inject
    public JIRAServiceImpl(final ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public String getBaseURL() {
        return this.applicationProperties.getString(APKeys.JIRA_BASEURL);
    }

    @Override
    public String getDateFormat(Date date) {
        String format = this.applicationProperties.getDefaultString("jira.lf.date.dmy");
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    @Override
    public String getDateTimeFormat(Date date) {
        String format = this.applicationProperties.getDefaultString("jira.lf.date.complete");
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

}