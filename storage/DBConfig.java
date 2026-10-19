package storage;

import java.io.FileInputStream;
import java.util.Properties;

public class DBConfig {
    private static final String PROPS = "data/db.properties";
    private static Properties props = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream(PROPS)) {
            props.load(fis);
        } catch (Exception e) {
            // use defaults
            props.setProperty("db.host", "localhost");
            props.setProperty("db.port", "3306");
            props.setProperty("db.name", "expense_manager");
            props.setProperty("db.user", "root");
            props.setProperty("db.password", "");
        }
    }

    public static String getHost() {
        return props.getProperty("db.host");
    }

    public static int getPort() {
        return Integer.parseInt(props.getProperty("db.port"));
    }

    public static String getName() {
        return props.getProperty("db.name");
    }

    public static String getUser() {
        return props.getProperty("db.user");
    }

    public static String getPassword() {
        return props.getProperty("db.password");
    }
}
