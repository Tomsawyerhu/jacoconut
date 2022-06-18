package config;

import org.apache.log4j.Logger;

public class Properties {
    private static Logger logger=Logger.getLogger(Properties.class);

    public static final String COV_KEY = "jacoconut.coverage";
    public static final String PROJECT_PREFIX_KEY = "project.prefix";

    public static String PROJECT_PREFIX = getProperty(PROJECT_PREFIX_KEY);
    public static String COV_TYPE = getProperty(COV_KEY);

    private static String getProperty(String key) {
        String result = null;
        if (System.getProperty(key) != null) {
            result = System.getProperty(key);
        }
        return result;
    }

    public static boolean getPropertyOrDefault(String key, boolean defaultValue) {
        String property = getProperty(key);
        logger.debug(key+"-"+property);
        if (property != null) {
            String propertyTrimmed = property.trim().toLowerCase();
            if (propertyTrimmed.equals("true") || propertyTrimmed.equals("yes")) {
                return true;
            } else if (propertyTrimmed.equals("false")
                    || propertyTrimmed.equals("no")) {
                return false;
            }
        }
        return defaultValue;
    }
}
