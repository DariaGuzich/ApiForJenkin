package api;

import api.UsersModels.UserAnswerModel;
import api.UsersModels.UserCreationModel;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    private static ReqresClient reqresClient;

    @BeforeAll
    public static void setupClient() {
        reqresClient = new ReqresClient();
    }

    @Test
    public void testGetValidUser() {
        int userId = 1;
        UserAnswerModel user = reqresClient.getUserById(userId);
        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertNotNull(user.getEmail());
    }

    @Test
    public void testCreateUser() {
        String name = "morpheus";
        String job = "leader";
        UserCreationModel userCreationModel = new UserCreationModel();
        userCreationModel.setName(name);
        userCreationModel.setJob(job);

        Response response = reqresClient.createUser(userCreationModel);
        assertEquals(201, response.getStatusCode());
    }

    @Test
    public void testDeleteUser() {
        String userId = "2";
        Response response = reqresClient.deleteUser(userId);
        assertEquals(204, response.getStatusCode());
    }
}
