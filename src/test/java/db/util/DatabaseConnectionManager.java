package db.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DatabaseConnectionManager {
    private static volatile DatabaseConnectionManager instance;
    private final ConcurrentMap<String, DataSource> dataSources = new ConcurrentHashMap<>();
    private final Properties dbProperties;

    private DatabaseConnectionManager() {
        this.dbProperties = loadDatabaseProperties();
    }

    public static DatabaseConnectionManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnectionManager.class) {
                if (instance == null) {
                    instance = new DatabaseConnectionManager();
                }
            }
        }
        return instance;
    }

    public DataSource getDataSource(String environment) {
        return dataSources.computeIfAbsent(environment, this::createDataSource);
    }

    private DataSource createDataSource(String environment) {
        String prefix = "db." + environment + ".";
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbProperties.getProperty(prefix + "url"));
        config.setUsername(dbProperties.getProperty(prefix + "username"));
        config.setPassword(dbProperties.getProperty(prefix + "password"));
        config.setDriverClassName(dbProperties.getProperty(prefix + "driver"));
        
        // Connection pool settings for test framework
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setPoolName(environment + "-pool");

        System.out.println("Creating connection pool for environment: " + environment);
        return new HikariDataSource(config);
    }

    private Properties loadDatabaseProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                System.out.println("database.properties not found, using defaults");
                return getDefaultProperties();
            }
            props.load(input);
            System.out.println("Loaded database configuration");
        } catch (IOException e) {
            System.out.println("Error loading database properties: " + e.getMessage());
            return getDefaultProperties();
        }
        return props;
    }

    private Properties getDefaultProperties() {
        Properties props = new Properties();
        // Default MySQL for local testing
        props.setProperty("db.local.url", "jdbc:mysql://localhost:3306/testdb");
        props.setProperty("db.local.username", "testuser");
        props.setProperty("db.local.password", "testpass");
        props.setProperty("db.local.driver", "com.mysql.cj.jdbc.Driver");
        return props;
    }

    public void closeAll() {
        dataSources.values().forEach(dataSource -> {
            if (dataSource instanceof HikariDataSource) {
                ((HikariDataSource) dataSource).close();
            }
        });
        dataSources.clear();
        System.out.println("All database connections closed");
    }
}