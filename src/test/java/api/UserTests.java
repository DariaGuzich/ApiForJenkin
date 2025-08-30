package api;

import api.UsersModels.UserAnswerModel;
import api.UsersModels.UserCreationModel;
import api.UsersModels.DeleteUserModel;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    private static DummyJsonClient dummyJsonClient;

    @BeforeAll
    public static void setupClient() {
        dummyJsonClient = new DummyJsonClient();
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
        UserCreationModel userCreationModel = new UserCreationModel();
        userCreationModel.setFirstName("Muhammad");
        userCreationModel.setLastName("Ovi");
        userCreationModel.setAge(25);

        Response response = dummyJsonClient.createUser(userCreationModel);
        assertEquals(201, response.getStatusCode());
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
