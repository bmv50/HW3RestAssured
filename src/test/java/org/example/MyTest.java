package org.example;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MyTest {
    static Properties prop = new Properties();
    static ResponseSpecification responseSpecification = null;
    static RequestSpecification requestSpecification = null;

    @BeforeAll
    static void setUp() throws IOException {
        responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectStatusLine("HTTP/1.1 200 OK")
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(5000L))
                .expectHeader("Access-Control-Allow-Credentials", "true")
                .build();

        String token = "Bearer f1f1f7c8eb4b2b12c79a9498262d45f8eefc795c";
        requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.ANY)
                .build();

        RestAssured.requestSpecification = requestSpecification;
        RestAssured.filters(new AllureRestAssured());
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        FileInputStream fis;
        fis = new FileInputStream("src/test/resources/my.properties");
        prop.load(fis);
    }
    String username = prop.getProperty("username");
    String albumHash = prop.getProperty("albumHash");
    String imageHash = prop.getProperty("imageHash");

    @Test
    void getAccountInfoTest() {
        String url = given()
                .when()
                .get("https://api.imgur.com/3/account/{username}", username)
                .then()
                .spec(responseSpecification)
                .contentType("application/json")
                .extract()
                .response()
                .jsonPath()
                .getString("data.url");
        assertThat(url, equalTo(username));

    }

    @Test
    void getVerifyEmailTest() {
        given()
                .when()
                .get("https://api.imgur.com/3/account/{username}/verifyemail", username)
                .then()
                .spec(responseSpecification);
    }

    @Test
    void postAlbumCreatingTest() {
        String idAlbum = given()
                .when()
                .post("https://api.imgur.com/3/album")
                .then()
                .spec(responseSpecification)
                .contentType("application/json")
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");
        Properties properties = new Properties();
        try(OutputStream outputStream = new FileOutputStream("src/test/resources/my.properties")){
            properties.setProperty("username", "bmv5081");
            properties.setProperty("imageHash", "IXjq3Tb");
            properties.setProperty("albumHash", idAlbum);
            properties.store(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getVerifyAlbumTest() {
        given()
                .when()
                .get("https://api.imgur.com/3/album/{albumHash}", albumHash)
                .then()
                .spec(responseSpecification);
    }

    @Test
    void getAlbumImagesTest() {
        given()
                .when()
                .get("https://api.imgur.com/3/album/{albumHash}/images", albumHash)
                .then()
                .spec(responseSpecification);
    }

    @Test
    void postFavoriteAlbumTest() {
        given()
                .when()
                .post("https://api.imgur.com/3/album/{albumHash}/favorite", albumHash)
                .then()
                .spec(responseSpecification);
    }
    @Test
    void deleteAlbumTest() {
        String delete = given()
                .when()
                .delete("https://api.imgur.com/3/album/{albumHash}", albumHash)
                .then()
                .spec(responseSpecification)
                .contentType("application/json")
                .extract()
                .response()
                .jsonPath()
                .getString("success");
        assertThat(delete, equalTo("true"));
    }
    @Test
    void getImageTest() {
        given()
                .when()
                .get("https://api.imgur.com/3/image/{imageHash}", imageHash)
                .then()
                .spec(responseSpecification);
    }
    @Test
    void postImageTest() {
        given()
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}", imageHash)
                .then()
                .statusCode(200);
    }
    @Test
    void postFavoriteImageTest() {
        given()
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}/favorite", imageHash)
                .then()
                .statusCode(200);
    }

    @BeforeAll
    static void beforeAll() {
     //for logging request and responses in Allure reporting
        RestAssured.filters(new AllureRestAssured());
    }

}
