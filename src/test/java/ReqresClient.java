import UsersModels.UserAnswerModel;
import UsersModels.UserCreationModel;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;

public class ReqresClient {

    private static final String BASE_URL = "https://reqres.in";
    private static final String USERS_ENDPOINT = "/api/users";

    protected RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .header("x-api-key", "reqres-free-v1")
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
        UserAnswerModel userAnswerModel = getUserByIdResponse(userId).jsonPath().getObject("data", UserAnswerModel.class);
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
                .post(USERS_ENDPOINT)
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
}
