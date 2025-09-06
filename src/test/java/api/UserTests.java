package api;

import api.UsersModels.UserAnswerModel;
import api.UsersModels.UserCreationModel;
import api.UsersModels.DeleteUserModel;
import db.util.DatabaseConnectionManager;
import db.util.DatabaseUtil;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    private static DummyJsonClient dummyJsonClient;
    private static DatabaseUtil dbUtil;

    @BeforeAll
    public static void setupClient() {
        dummyJsonClient = new DummyJsonClient();
        
        // Initialize database utility for Docker environment
        String environment = System.getProperty("test.db.environment", "docker");
        dbUtil = new DatabaseUtil(environment);
        System.out.println("Database tests initialized for environment: " + environment);
    }
    
    @AfterAll
    public static void cleanup() {
        // Close database connections
        DatabaseConnectionManager.getInstance().closeAll();
    }

    @Test
    public void testGetValidUser() {
        int userId = 1;
        UserAnswerModel user = dummyJsonClient.getUserById(userId);
        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertNotNull(user.getEmail());
    }

    @Test
    public void testCreateUser() {
        // API Test - Create user via API
        UserCreationModel userCreationModel = new UserCreationModel();
        userCreationModel.setFirstName("Muhammad");
        userCreationModel.setLastName("Ovi");
        userCreationModel.setAge(25);

        Response response = dummyJsonClient.createUser(userCreationModel);
        assertEquals(201, response.getStatusCode());
        System.out.println("API Test: User creation API call successful");
        
        // Database Verification - Check if user Muhammad Ovi exists in database
        System.out.println("Database Test: Verifying Muhammad Ovi exists in database...");
        
        // Check if user exists by firstname and lastname
        boolean userExists = dbUtil.recordExists("users", 
            "firstname = ? AND lastname = ?", "Muhammad", "Ovi");
        assertTrue(userExists, "User Muhammad Ovi should exist in database");
        
        // Get the user details for detailed verification
        Map<String, Object> user = dbUtil.executeQueryForSingleRow(
            "SELECT * FROM users WHERE firstname = ? AND lastname = ?", 
            "Muhammad", "Ovi");
        
        assertNotNull(user, "User should be found in database");
        assertEquals("Muhammad", user.get("firstname"));
        assertEquals("Ovi", user.get("lastname"));
        assertEquals(25, user.get("age"));
        assertEquals("muhammad.ovi@example.com", user.get("email"));
        
        System.out.println("Database Test: User verification completed successfully");
        System.out.println("Found user: " + user);
    }

    @Test
    public void testDeleteUser() {
        String userId = "1";
        
        DeleteUserModel deletedUser = dummyJsonClient.deleteUserAndGetModel(userId);
        assertEquals(Integer.parseInt(userId), deletedUser.getId());
        assertTrue(deletedUser.isDeleted());
        assertNotNull(deletedUser.getDeletedOn());
        assertNotNull(deletedUser.getFirstName());
        assertNotNull(deletedUser.getEmail());
    }
}
