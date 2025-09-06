package api;

import api.UsersModels.UserAnswerModel;
import api.UsersModels.UserCreationModel;
import api.UsersModels.DeleteUserModel;
import db.UserRepository;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    private static DummyJsonClient dummyJsonClient;
    private static UserRepository userRepository;

    @BeforeAll
    public static void setupClient() {
        dummyJsonClient = new DummyJsonClient();
        String environment = System.getProperty("test.db.environment", System.getenv("JENKINS_URL") != null ? "jenkins" : "docker");
        userRepository = new UserRepository(environment);
        System.out.println("Database tests initialized for environment: " + environment);
    }
    
    @AfterAll
    public static void cleanup() {
        UserRepository.closeAllConnections();
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
        
        // Database Verification - Verify Muhammad Ovi exists in database
        System.out.println("Database Test: Verifying Muhammad Ovi exists in database...");
        
        // Validate user exists with expected properties
        userRepository.validateUser("Muhammad", "Ovi", 25, "muhammad.ovi@example.com");
        
        System.out.println("Database Test: User verification completed successfully");
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
