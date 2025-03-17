import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;


public class ChangeUserDataTest extends HelperUtilsClass{

    @Test
    @DisplayName("Изменение данных с авторизацией")
    public void testUpdateUserWithAuthorization() {
        String email = "existing_user@example.com";
        String password = "password123";
        String newName = "Updated Name";

        String token = loginAndGetToken(email, password);
        Response response = updateUser(token, newName);
        verifyUserUpdateSuccess(response, newName);
    }

    @Step("Логин и получение токена")
    public String loginAndGetToken(String email, String password) {
        String requestBody = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);
        String endpoint = BASE_URL + "/auth/login";
        return given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(endpoint)
                .then()
                .extract()
                .path("accessToken");
    }

    @Step("Изменение данных пользователя с токеном: {token}, новое имя: {newName}")
    private Response updateUser(String token, String newName) {
        String requestBody = String.format("{\"name\": \"%s\"}", newName);
        String endpoint = BASE_URL + "/auth/user";
        return given()
                .contentType("application/json")
                .header("Authorization", token)
                .body(requestBody)
                .when()
                .patch(endpoint);
    }

    @Step("Проверка успешного изменения данных")
    private void verifyUserUpdateSuccess(Response response, String newName) {
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.name", equalTo(newName));
    }
}