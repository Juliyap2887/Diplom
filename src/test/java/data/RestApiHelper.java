package data;

import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class RestApiHelper {
    static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(8080)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    private static final Gson gson = new Gson();
    private RestApiHelper() {}

    public static void sendPaymentRequest(CardInfo cardInfo, String path, int status) {
        given()
                .spec(requestSpec)
                .body(gson.toJson(cardInfo))
                .when()
                .post(path)
                .then().log().all()
                .statusCode(status);
    }

    public static void sendPaymentRequestOnCredit(CardInfo cardInfo, String path, int status) {
        given()
                .spec(requestSpec)
                .body(gson.toJson(cardInfo))
                .when()
                .post(path)
                .then().log().all()
                .statusCode(status);
    }

}
