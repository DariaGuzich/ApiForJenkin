package api;

import api.UsersModels.UserAnswerModel;
import api.UsersModels.UserCreationModel;
import api.UsersModels.DeleteUserModel;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;

public class DummyJsonClient {

    private static final String BASE_URL = "https://dummyjson.com";
    private static final String USERS_ENDPOINT = "/users";

    protected RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json");
    }

    public Response getUserByIdResponse(int userId) {
        Response response = getRequestSpec()
                .pathParam("id", userId)
                .when()
                .get(USERS_ENDPOINT + "/{id}")
                .then()
                .extract()
                .response();

        return response;
    }

    public UserAnswerModel getUserById(int userId) {
        UserAnswerModel userAnswerModel = getUserByIdResponse(userId).jsonPath().getObject("", UserAnswerModel.class);
        Assertions.assertNotNull(userAnswerModel, "User should not be null");
        return userAnswerModel;
    }

    public Response getListOfUsers() {
        Response response = getRequestSpec()
                .when()
                .get(USERS_ENDPOINT)
                .then()
                .extract()
                .response();

        return response;
    }

    public Response createUser(UserCreationModel userCreationModel) {
        Response response = getRequestSpec()
                .when()
                .body(userCreationModel)
                .post(USERS_ENDPOINT + "/add")
                .then()
                .extract()
                .response();

        return response;
    }

    public Response deleteUser(String userId) {
        Response response = getRequestSpec()
                .when()
                .delete(USERS_ENDPOINT + "/" + userId)
                .then()
                .extract()
                .response();

        return response;
    }

    public DeleteUserModel deleteUserAndGetModel(String userId) {
        Response response = deleteUser(userId);
        Assertions.assertEquals(200, response.getStatusCode(), "Delete request should return 200 status");
        
        DeleteUserModel deleteUserModel = response.jsonPath().getObject("", DeleteUserModel.class);
        Assertions.assertNotNull(deleteUserModel, "Delete response should not be null");
        return deleteUserModel;
    }
}
