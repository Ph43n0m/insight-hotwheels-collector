package collector.hotwheels.insight.imports.model.locators.base;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class LocatorHelper {

    private static final Logger logger = LoggerFactory.getLogger(LocatorHelper.class);

    public static Object tryGetValue(Object obj, String fieldName) {
        Object value;
        try {
            value = PropertyUtils.getProperty(obj, fieldName);
        } catch (Exception ignore) {
            value = null;
        }

        return value;
    }

    public static void getPropertyNames(Class<?> getClass, Set<String> propertyNames) {

        if (propertyNames == null) {
            propertyNames = new HashSet<>();
        }

        if (getClass != null && !getClass.getName()
                .equals("java.lang.Object")) {

            try {
                addFields(getClass.getDeclaredFields(), propertyNames);

                if (getClass.getAnnotatedSuperclass() != null && !getClass.getAnnotatedSuperclass()
                        .getClass()
                        .equals(getClass)) {
                    try {
                        Class<?> clazz = Class.forName(getClass.getAnnotatedSuperclass()
                                .getType()
                                .getTypeName());
                        if (clazz != null) {
                            getPropertyNames(clazz, propertyNames);
                        }
                    } catch (ClassNotFoundException ignore) {
                    } catch (Exception ex) {
                        logger.error("AnnotatedType error.", ex);
                    }
                }
            } catch (Exception ex) {
                logger.error("Error getting property names for: " + getClass.getName(), ex);
            }
        }
    }

    private static void addFields(Field[] fields, Set<String> values) {
        try {
            for (Field field : fields) {
                values.add(field.getName());
            }
        } catch (Exception ex) {
            logger.error("Error adding field names", ex);
        }
    }
}
