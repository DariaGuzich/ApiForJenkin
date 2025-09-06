package db.util;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class DatabaseUtil {
    private final JdbcTemplate jdbcTemplate;
    private final String environment;

    public DatabaseUtil(String environment) {
        this.environment = environment;
        DataSource dataSource = DatabaseConnectionManager.getInstance().getDataSource(environment);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        System.out.println("DatabaseUtil initialized for environment: " + environment);
    }

    // Common query operations for test verification
    public List<Map<String, Object>> executeQuery(String sql, Object... params) {
        try {
            // System.out.println("Executing query: " + sql);
            return jdbcTemplate.queryForList(sql, params);
        } catch (Exception e) {
            System.out.println("Error executing query: " + sql + " - " + e.getMessage());
            throw new RuntimeException("Database query failed: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> executeQueryForSingleRow(String sql, Object... params) {
        List<Map<String, Object>> results = executeQuery(sql, params);
        if (results.isEmpty()) {
            return null;
        }
        if (results.size() > 1) {
            System.out.println("Query returned " + results.size() + " rows, expected 1. Using first row.");
        }
        return results.get(0);
    }

    public <T> T executeQueryForObject(String sql, Class<T> requiredType, Object... params) {
        try {
            // System.out.println("Executing query for object: " + sql);
            return jdbcTemplate.queryForObject(sql, requiredType, params);
        } catch (Exception e) {
            System.out.println("Error executing query for object: " + sql + " - " + e.getMessage());
            throw new RuntimeException("Database query failed: " + e.getMessage(), e);
        }
    }

    public int executeUpdate(String sql, Object... params) {
        try {
            // System.out.println("Executing update: " + sql);
            int rowsAffected = jdbcTemplate.update(sql, params);
            // System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected;
        } catch (Exception e) {
            System.out.println("Error executing update: " + sql + " - " + e.getMessage());
            throw new RuntimeException("Database update failed: " + e.getMessage(), e);
        }
    }

    public int getRowCount(String tableName) {
        return executeQueryForObject("SELECT COUNT(*) FROM " + tableName, Integer.class);
    }

    public int getRowCount(String tableName, String whereCondition, Object... params) {
        return executeQueryForObject("SELECT COUNT(*) FROM " + tableName + " WHERE " + whereCondition, 
            Integer.class, params);
    }

    public boolean recordExists(String tableName, String whereCondition, Object... params) {
        return getRowCount(tableName, whereCondition, params) > 0;
    }

    // Common test data setup/cleanup methods
    public void cleanupTable(String tableName) {
        executeUpdate("DELETE FROM " + tableName);
        System.out.println("Cleaned up table: " + tableName);
    }

    public void cleanupTable(String tableName, String whereCondition, Object... params) {
        int deleted = executeUpdate("DELETE FROM " + tableName + " WHERE " + whereCondition, params);
        System.out.println("Cleaned up " + deleted + " rows from table: " + tableName);
    }

    // Utility methods for test assertions
    public void assertRecordExists(String tableName, String whereCondition, Object... params) {
        if (!recordExists(tableName, whereCondition, params)) {
            throw new AssertionError("Expected record not found in " + tableName + " with condition: " + whereCondition);
        }
    }

    public void assertRecordNotExists(String tableName, String whereCondition, Object... params) {
        if (recordExists(tableName, whereCondition, params)) {
            throw new AssertionError("Unexpected record found in " + tableName + " with condition: " + whereCondition);
        }
    }

    public void assertRowCount(String tableName, int expectedCount) {
        int actualCount = getRowCount(tableName);
        if (actualCount != expectedCount) {
            throw new AssertionError("Expected " + expectedCount + " rows in " + tableName + ", but found " + actualCount);
        }
    }

    public String getEnvironment() {
        return environment;
    }
}