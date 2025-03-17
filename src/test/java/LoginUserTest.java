import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;


public class LoginUserTest extends HelperUtilsClass {

    @Test
    @DisplayName("Логин под существующим пользователем")
    public void testLoginWithExistingUser() {
        String email = "existing_user@example.com";
        String password = "password123";

        Response response = loginUser(email, password);
        verifyLoginSuccess(response);
    }
    @Test
    @DisplayName("Логин с неверным логином и паролем")
    public void testLoginWithInvalidCredentials() {
        String email = "invalid_user@example.com"; // Неверный email
        String password = "wrong_password"; // Неверный пароль

        Response response = loginUser(email, password);
        verifyLoginFailure(response);
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    public void testUpdateUserWithoutAuthorization() {
        String newName = "Unauthorized Update";

        Response response = updateUserWithoutAuthorization(newName);
        verifyUnauthorizedError(response);
    }

    @Step("Отправка запроса на изменение данных пользователя без авторизации")
    private Response updateUserWithoutAuthorization(String newName) {
        String requestBody = String.format("{\"name\": \"%s\"}", newName);
        String endpoint = BASE_URL + "/auth/user";
        return given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .patch(endpoint);
    }

    @Step("Проверка ошибки при отсутствии авторизации")
    private void verifyUnauthorizedError(Response response) {
        response.then()
                .statusCode(SC_UNAUTHORIZED) // Ожидаемый статус код для неавторизованного запроса
                .body("message", equalTo("You should be authorised")); // Ожидаемое сообщение об ошибке
    }

    @Step("Логин пользователя с email: {email}, паролем: {password}")
    private Response loginUser(String email, String password) {
        String requestBody = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);
        String endpoint = BASE_URL + "/auth/login";
        return given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(endpoint);
    }

    @Step("Проверка ошибки при логине с неверными учетными данными")
    private void verifyLoginFailure(Response response) {
        response.then()
                .statusCode(SC_UNAUTHORIZED) // Ожидаемый статус код для неверных учетных данных
                .body("message", equalTo("email or password are incorrect")); // Ожидаемое сообщение об ошибке
    }

    @Step("Проверка успешного логина")
    private void verifyLoginSuccess(Response response) {
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }




}