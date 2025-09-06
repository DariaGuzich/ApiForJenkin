package db;

import db.util.DatabaseConnectionManager;
import db.util.DatabaseUtil;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseVerificationTests {
    
    private static DatabaseUtil dbUtil;
    
    @BeforeAll
    static void setUp() {
        // Initialize database utility for the desired environment
        String environment = System.getProperty("test.db.environment", "local");
        dbUtil = new DatabaseUtil(environment);
        System.out.println("Database tests initialized for environment: " + environment);
        
        // Setup test data if needed
        setupTestData();
    }
    
    @AfterAll
    static void tearDown() {
        // Clean up test data if needed
        cleanupTestData();
        
        // Close all database connections
        DatabaseConnectionManager.getInstance().closeAll();
    }
    
    private static void setupTestData() {
        // Create test table for demonstration (if using H2)
        try {
            dbUtil.executeUpdate("""
                CREATE TABLE IF NOT EXISTS test_users (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    email VARCHAR(100) NOT NULL,
                    status VARCHAR(20) DEFAULT 'ACTIVE'
                )
            """);
            
            // Insert test data
            dbUtil.executeUpdate("INSERT INTO test_users (username, email, status) VALUES (?, ?, ?)",
                "testuser1", "test1@example.com", "ACTIVE");
            dbUtil.executeUpdate("INSERT INTO test_users (username, email, status) VALUES (?, ?, ?)",
                "testuser2", "test2@example.com", "INACTIVE");
                
            System.out.println("Test data setup completed");
        } catch (Exception e) {
            System.out.println("Could not setup test data - may be connecting to external DB: " + e.getMessage());
        }
    }
    
    private static void cleanupTestData() {
        try {
            dbUtil.cleanupTable("test_users");
            System.out.println("Test data cleanup completed");
        } catch (Exception e) {
            System.out.println("Could not cleanup test data: " + e.getMessage());
        }
    }
    
    @Test
    @Order(1)
    @DisplayName("Verify user exists in database")
    void testUserExistsInDatabase() {
        // Example: Verify a user exists after API call
        boolean userExists = dbUtil.recordExists("test_users", "username = ?", "testuser1");
        assertTrue(userExists, "User testuser1 should exist in database");
        
        // Verify user details
        Map<String, Object> user = dbUtil.executeQueryForSingleRow(
            "SELECT * FROM test_users WHERE username = ?", "testuser1");
        
        assertNotNull(user);
        assertEquals("test1@example.com", user.get("EMAIL"));
        assertEquals("ACTIVE", user.get("STATUS"));
        
        System.out.println("User verification completed: " + user);
    }
    
    @Test
    @Order(2)
    @DisplayName("Verify user count after operations")
    void testUserCountAfterOperations() {
        // Example: Verify correct number of users after bulk operations
        int initialCount = dbUtil.getRowCount("test_users");
        
        // Simulate adding more users (normally done by your application)
        dbUtil.executeUpdate("INSERT INTO test_users (username, email) VALUES (?, ?)",
            "newuser", "new@example.com");
        
        int finalCount = dbUtil.getRowCount("test_users");
        assertEquals(initialCount + 1, finalCount, "User count should increase by 1");
        
        // Verify specific conditions
        int activeUsers = dbUtil.getRowCount("test_users", "status = ?", "ACTIVE");
        assertTrue(activeUsers >= 2, "Should have at least 2 active users");
    }
    
    @Test
    @Order(3)
    @DisplayName("Verify data integrity after API operations")
    void testDataIntegrityAfterAPIOperations() {
        // Example: After calling API to update user status, verify in DB
        
        // Simulate API call result verification
        List<Map<String, Object>> users = dbUtil.executeQuery(
            "SELECT username, email, status FROM test_users WHERE status = ?", "ACTIVE");
        
        assertFalse(users.isEmpty(), "Should have active users");
        
        // Verify each user has required fields
        for (Map<String, Object> user : users) {
            assertNotNull(user.get("USERNAME"), "Username should not be null");
            assertNotNull(user.get("EMAIL"), "Email should not be null");
            assertTrue(user.get("EMAIL").toString().contains("@"), "Email should be valid");
        }
        
        System.out.println("Found " + users.size() + " active users");
    }
    
    @Test
    @Order(4)
    @DisplayName("Verify database state cleanup")
    void testDatabaseStateCleanup() {
        // Example: Verify cleanup operations worked correctly
        
        // Clean up specific test data
        dbUtil.cleanupTable("test_users", "username LIKE ?", "temp%");
        
        // Verify cleanup
        dbUtil.assertRecordNotExists("test_users", "username LIKE ?", "temp%");
        
        // Verify original data still exists
        dbUtil.assertRecordExists("test_users", "username = ?", "testuser1");
        
        System.out.println("Database cleanup verification completed");
    }
    
    @Test
    @DisplayName("Verify complex query results")
    void testComplexQueryResults() {
        // Example: Complex verification queries
        List<Map<String, Object>> results = dbUtil.executeQuery("""
            SELECT 
                status, 
                COUNT(*) as user_count,
                GROUP_CONCAT(username) as usernames
            FROM test_users 
            GROUP BY status
            ORDER BY status
        """);
        
        assertFalse(results.isEmpty(), "Should have grouped results");
        
        for (Map<String, Object> row : results) {
            String status = (String) row.get("STATUS");
            Number count = (Number) row.get("USER_COUNT");
            
            assertNotNull(status);
            assertNotNull(count);
            assertTrue(count.intValue() > 0);
            
            System.out.println("Status: " + status + ", Count: " + count);
        }
    }
}