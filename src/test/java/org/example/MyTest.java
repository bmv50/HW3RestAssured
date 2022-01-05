package org.example;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MyTest {
    static Map<String, String> headers = new HashMap<>();
    static Properties prop = new Properties();

    @BeforeAll
    static void setUp() throws IOException {
        RestAssured.filters(new AllureRestAssured());
        headers.put("Authorization", "Bearer f1f1f7c8eb4b2b12c79a9498262d45f8eefc795c");
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
                .headers(headers)
                .when()
                .get("https://api.imgur.com/3/account/{username}", username)
                .then()
                .statusCode(200)
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
                .headers(headers)
                .when()
                .get("https://api.imgur.com/3/account/{username}/verifyemail", username)
                .then()
                .statusCode(200);
    }

    @Test
    void postAlbumCreatingTest() throws IOException {
        String idAlbum = given()
                .headers(headers)
                .when()
                .post("https://api.imgur.com/3/album")
                .then()
                .statusCode(200)
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
                .headers(headers)
                .when()
                .get("https://api.imgur.com/3/album/{albumHash}", albumHash)
                .then()
                .statusCode(200);
    }

    @Test
    void getAlbumImagesTest() {
        given()
                .headers(headers)
                .when()
                .get("https://api.imgur.com/3/album/{albumHash}/images", albumHash)
                .then()
                .statusCode(200);
    }

    @Test
    void postFavoriteAlbumTest() {
        given()
                .headers(headers)
                .when()
                .post("https://api.imgur.com/3/album/{albumHash}/favorite", albumHash)
                .then()
                .statusCode(200);
    }
    @Test
    void deleteAlbumTest() {
        String delete = given()
                .headers(headers)
                .when()
                .delete("https://api.imgur.com/3/album/{albumHash}", albumHash)
                .then()
                .statusCode(200)
                .statusCode(200)
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
                .headers(headers)
                .when()
                .get("https://api.imgur.com/3/image/{imageHash}", imageHash)
                .then()
                .statusCode(200);
    }
    @Test
    void postImageTest() {
        given()
                .headers(headers)
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}", imageHash)
                .then()
                .statusCode(200);
    }
    @Test
    void postFavoriteImageTest() {
        given()
                .headers(headers)
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
