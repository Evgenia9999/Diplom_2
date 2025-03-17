import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;


public class GetOrderFromExactUserTest extends HelperUtilsClass {

    String endpoint = BASE_URL + "/orders";

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    public void testGetOrdersWithAuthorization() {
        String email = "existing_user@example.com";
        String password = "password123";

        String token = loginAndGetToken(email, password);
        Response response = getOrders(token);
        verifyOrdersRetrievedSuccessfully(response);
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    public void testGetOrdersWithoutAuthorization() {
        Response response = getOrdersWithoutAuthorization();
        verifyUnauthorizedError(response);
    }

    @Step("Отправка запроса на получение заказов без авторизации")
    private Response getOrdersWithoutAuthorization() {
        return given()
                .contentType("application/json")
                .when()
                .get(endpoint);
    }

    @Step("Проверка ошибки при отсутствии авторизации")
    private void verifyUnauthorizedError(Response response) {
        response.then()
                .statusCode(SC_UNAUTHORIZED) // Ожидаемый статус код для неавторизованного запроса
                .body("message", equalTo("You should be authorised")); // Ожидаемое сообщение об ошибке
    }

    @Step("Получение заказов с токеном: {token}")
    private Response getOrders(String token) {

        return given()
                .contentType("application/json")
                .header("Authorization", token)
                .when()
                .get(endpoint);
    }

    @Step("Проверка успешного получения заказов")
    private void verifyOrdersRetrievedSuccessfully(Response response) {
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }
}