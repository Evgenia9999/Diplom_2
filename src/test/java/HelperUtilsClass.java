import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;


public class HelperUtilsClass {

    public String BASE_URL = "https://stellarburgers.nomoreparties.site/api";

    // Метод для логина и получения токена
    public String loginAndGetToken(String email, String password) {
        String requestBody = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);
        String endpoint = BASE_URL + "/auth/login";
        Response response = given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(endpoint);

        return response.then()
                .extract()
                .path("accessToken");
    }
}
