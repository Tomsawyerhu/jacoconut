package storage;

import java.util.Properties;

public class Property {
    public static Properties properties;
    public static final String PROJECT_PREFIX_KEY="jacoconut.project.prefix";
    public static final String METHOD_COV_KEY = "coverage.method";
    public static final String LINE_COV_KEY = "coverage.line";
    public static final String BRANCH_COV_KEY = "coverage.branch";

    public static String PROJECT_PREFIX=getProperty(PROJECT_PREFIX_KEY);
    public static boolean METHOD_COV = getPropertyOrDefault(METHOD_COV_KEY,
            false);
    public static boolean LINE_COV = getPropertyOrDefault(LINE_COV_KEY,
            false);
    public static boolean BRANCH_COV = getPropertyOrDefault(BRANCH_COV_KEY,
            false);

    public static String getProperty(String key) {
        String result = null;
        if (System.getProperty(key) != null) {
            result = System.getProperty(key);
        }
        if(properties!=null&&properties.getProperty(key)!=null){
            result=properties.getProperty(key);
        }
        return result;
    }

    public static boolean getPropertyOrDefault(String key, boolean defaultValue) {
        String property = getProperty(key);
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
