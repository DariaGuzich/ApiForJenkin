package db.util;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class DatabaseUtil {
    private final JdbcTemplate jdbcTemplate;

    public DatabaseUtil(String environment) {
        DataSource dataSource = DatabaseConnectionManager.getInstance().getDataSource(environment);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        System.out.println("DatabaseUtil initialized for environment: " + environment);
    }

    public Map<String, Object> executeQueryForSingleRow(String sql, Object... params) {
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);
        if (results.isEmpty()) {
            return null;
        }
        if (results.size() > 1) {
            System.out.println("Query returned " + results.size() + " rows, expected 1. Using first row.");
        }
        return results.get(0);
    }

    public boolean recordExists(String tableName, String whereCondition, Object... params) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName + " WHERE " + whereCondition, 
            Integer.class, params);
        return count != null && count > 0;
    }
}