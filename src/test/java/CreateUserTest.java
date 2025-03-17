import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;


public class CreateUserTest extends HelperUtilsClass {



    @Test
    @DisplayName("Создание уникального пользователя")
    public void testCreateUniqueUser() {
        String email = generateUniqueEmail();
        String password = "password123";
        String name = "Unique User";

        Response response = createUser(email, password, name);
        verifyUserCreatedSuccessfully(response);
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void testCreateExistingUser() {
        String email = "existing_user@example.com"; // Используем уже зарегистрированный email
        String password = "password123";
        String name = "Existing User";

        Response response = createUser(email, password, name);
        verifyUserAlreadyExists(response);
    }

    @Test
    @DisplayName("Создание пользователя без заполнения обязательного поля")
    public void testCreateUserWithoutRequiredField() {
        String email = "user_without_password7@example.com";
        String name = "User Without Password7";

        // Пароль не передаем (обязательное поле)
        Response response = createUser(email, null, name);
        verifyRequiredFieldError(response);
    }

    @Step("Создание пользователя с email: {email}, паролем: {password}, именем: {name}")
    private Response createUser(String email, String password, String name) {
        String requestBody = String.format("{\"email\": \"%s\", \"password\": \"%s\", \"name\": \"%s\"}", email, password, name);
        String endpoint = BASE_URL + "/auth/register";
        return given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(endpoint);
    }

    @Step("Проверка успешного создания пользователя")
    private void verifyUserCreatedSuccessfully(Response response) {
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Step("Генерация уникального email")
    private String generateUniqueEmail() {
        return "unique_user_" + System.currentTimeMillis() + "@example.com";
    }

    @Step("Проверка ошибки при создании уже зарегистрированного пользователя")
    private void verifyUserAlreadyExists(Response response) {
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("User already exists"));
    }

    @Step("Проверка ошибки при отсутствии обязательного поля")
    private void verifyRequiredFieldError(Response response) {
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

}