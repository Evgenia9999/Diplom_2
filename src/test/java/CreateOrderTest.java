import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;



public class CreateOrderTest extends HelperUtilsClass {

    String endpoint = BASE_URL + "/orders";

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    public void testCreateOrderWithAuthorizationAndIngredients() {
        String email = "existing_user@example.com";
        String password = "password123";
        String[] ingredients = {"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6e"};

        String token = loginAndGetToken(email, password);
        Response response = createOrder(token, ingredients);
        verifyOrderCreationSuccess(response);
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void testCreateOrderWithoutAuthorization() {
        String[] ingredients = {"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6e"}; // Пример ингредиентов

        Response response = createOrderWithoutAuthorization(ingredients);
        verifyUnauthorizedError(response);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void testCreateOrderWithoutIngredients() {
        Response response = createOrderWithoutIngredients();
        verifyMissingIngredientsError(response);
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void testCreateOrderWithInvalidIngredientHash() {
        String[] invalidIngredients = {"invalid_hash_1", "invalid_hash_2"}; // Неверные хеши ингредиентов

        Response response = createOrderWithInvalidIngredients(invalidIngredients);
        verifyInvalidIngredientsError(response);
    }

    @Step("Отправка запроса на создание заказа с неверным хешем ингредиентов")
    private Response createOrderWithInvalidIngredients(String[] ingredients) {
        String requestBody = String.format("{\"ingredients\": [\"%s\", \"%s\"]}", ingredients[0], ingredients[1]);
        return given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(endpoint);
    }

    @Step("Проверка ошибки при неверном хеше ингредиентов")
    private void verifyInvalidIngredientsError(Response response) {
        response.then()
                .statusCode(SC_INTERNAL_SERVER_ERROR); // Ожидаемый
    }

    @Step("Отправка запроса на создание заказа без ингредиентов")
    private Response createOrderWithoutIngredients() {
        String requestBody = "{\"ingredients\": []}";
        return given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(endpoint);
    }

    @Step("Проверка ошибки при отсутствии ингредиентов")
    private void verifyMissingIngredientsError(Response response) {
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Step("Отправка запроса на создание заказа без авторизации")
    private Response createOrderWithoutAuthorization(String[] ingredients) {
        String requestBody = String.format("{\"ingredients\": [\"%s\", \"%s\"]}", ingredients[0], ingredients[1]);
        return given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(endpoint);
    }

    @Step("Проверка ошибки при отсутствии авторизации")
    private void verifyUnauthorizedError(Response response) {
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("message", equalTo("You should be authorised"));
    }

    @Step("Создание заказа с токеном: {token}, ингредиенты: {ingredients}")
    private Response createOrder(String token, String[] ingredients) {
        String requestBody = String.format("{\"ingredients\": [\"%s\", \"%s\"]}", ingredients[0], ingredients[1]);

        return given()
                .contentType("application/json")
                .header("Authorization", token)
                .body(requestBody)
                .when()
                .post(endpoint);
    }

    @Step("Проверка успешного создания заказа")
    private void verifyOrderCreationSuccess(Response response) {
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

}

