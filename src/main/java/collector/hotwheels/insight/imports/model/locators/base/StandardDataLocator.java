package collector.hotwheels.insight.imports.model.locators.base;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static collector.hotwheels.insight.imports.model.locators.base.LocatorHelper.tryGetValue;
import static collector.hotwheels.insight.util.StringUtils.isNullOrEmpty;

public class StandardDataLocator extends ModuleDataLocator {

    private static final Logger logger = LoggerFactory.getLogger(StandardDataLocator.class);

    private String methodName;

    public StandardDataLocator(String locator, String methodName) {
        super(locator);
        super.locatorType = LocatorType.JAVA_METHOD_VALUE;
        this.methodName = methodName;
    }

    public List<String> getMethodValue(Object object) {
        try {
            Object value = null;
            Object subClass = object;
            String restMethodName = methodName;

            while (restMethodName.contains(".")) {
                subClass = tryGetValue(subClass, restMethodName.substring(0, restMethodName.indexOf(".")));
                restMethodName = restMethodName.substring(restMethodName.indexOf(".") + 1);
            }

            if (subClass != null && !isNullOrEmpty(restMethodName)) {
                value = tryGetValue(subClass, restMethodName);
            }

            if (value != null) {
                return Arrays.asList(value.toString());
            }
        } catch (Exception ee) {
            logger.warn("Could not get locator value", ee);
        }
        return Lists.newArrayList();
    }

    public List<String> getMethodValue(Object object, String methodName) {
        try {
            Object value = tryGetValue(object, methodName);
            if (value != null) {
                return Arrays.asList(value.toString());
            }
        } catch (Exception ee) {
            logger.warn("Could not get locator value", ee);
        }
        return Lists.newArrayList();
    }
}
