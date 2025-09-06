package db;

import db.util.DatabaseConnectionManager;
import db.util.DatabaseUtil;

import java.util.Map;
import java.util.Optional;

/**
 * Repository class for User table operations
 * Provides high-level methods for user-related database operations
 */
public class UserRepository {
    
    private final DatabaseUtil dbUtil;
    
    /**
     * Create UserRepository with environment-specific database configuration
     * @param environment database environment (local, docker, jenkins, etc.)
     */
    public UserRepository(String environment) {
        this.dbUtil = new DatabaseUtil(environment);
    }

    /**
     * Check if user exists by first name and last name
     */
    public boolean userExists(String firstName, String lastName) {
        return dbUtil.recordExists("users", 
            "firstname = ? AND lastname = ?", firstName, lastName);
    }
    
    /**
     * Find user by first name and last name
     * @return Optional containing user data if found, empty otherwise
     */
    public Optional<User> findByName(String firstName, String lastName) {
        Map<String, Object> userData = dbUtil.executeQueryForSingleRow(
            "SELECT * FROM users WHERE firstname = ? AND lastname = ?", 
            firstName, lastName);
        
        if (userData == null) {
            return Optional.empty();
        }
        
        return Optional.of(mapToUser(userData));
    }
    
    /**
     * Find user by ID
     */
    public Optional<User> findById(Long id) {
        Map<String, Object> userData = dbUtil.executeQueryForSingleRow(
            "SELECT * FROM users WHERE id = ?", id);
        
        if (userData == null) {
            return Optional.empty();
        }
        
        return Optional.of(mapToUser(userData));
    }
    
    /**
     * Validate that user exists with expected properties
     */
    public void validateUser(String firstName, String lastName, int expectedAge, String expectedEmail) {
        Optional<User> userOpt = findByName(firstName, lastName);
        
        if (userOpt.isEmpty()) {
            throw new AssertionError("User with name " + firstName + " " + lastName + " not found");
        }
        
        User user = userOpt.get();
        
        if (!firstName.equals(user.getFirstName())) {
            throw new AssertionError("Expected firstName: " + firstName + ", but was: " + user.getFirstName());
        }
        
        if (!lastName.equals(user.getLastName())) {
            throw new AssertionError("Expected lastName: " + lastName + ", but was: " + user.getLastName());
        }
        
        if (expectedAge != user.getAge()) {
            throw new AssertionError("Expected age: " + expectedAge + ", but was: " + user.getAge());
        }
        
        if (!expectedEmail.equals(user.getEmail())) {
            throw new AssertionError("Expected email: " + expectedEmail + ", but was: " + user.getEmail());
        }
    }
    
    /**
     * Map database row to User object
     */
    private User mapToUser(Map<String, Object> userData) {
        User user = new User();
        user.setId(((Number) userData.get("id")).longValue());
        user.setFirstName((String) userData.get("firstname"));
        user.setLastName((String) userData.get("lastname"));
        user.setAge(((Number) userData.get("age")).intValue());
        user.setEmail((String) userData.get("email"));
        return user;
    }
    
    /**
     * Close all database connections
     * Should be called in test cleanup methods
     */
    public static void closeAllConnections() {
        DatabaseConnectionManager.getInstance().closeAll();
    }
    
    /**
     * Inner class representing User entity
     */
    public static class User {
        private Long id;
        private String firstName;
        private String lastName;
        private Integer age;
        private String email;
        
        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", age=" + age +
                    ", email='" + email + '\'' +
                    '}';
        }
    }
}