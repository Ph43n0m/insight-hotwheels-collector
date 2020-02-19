package collector.hotwheels.insight.services;

import com.atlassian.jira.propertyset.JiraPropertySetFactory;
import com.opensymphony.module.propertyset.PropertySet;

public class PropertyManagerImpl implements PropertyManager {

    public static final String LICENSE_KEY = "LICENSE_KEY";
    private final PropertySet propertySet;

    public PropertyManagerImpl(JiraPropertySetFactory jiraPropertySetFactory) {
        this.propertySet = jiraPropertySetFactory.buildNoncachingPropertySet("INSIGHT-HOTWHEELS-COLLECTOR");

    }

    @Override
    public String getLicenseKey() {
        return this.propertySet.getText(LICENSE_KEY);
    }

    @Override
    public void setLicenseKey(String licenseKey) {
        this.propertySet.setText(LICENSE_KEY, licenseKey);
    }

}
