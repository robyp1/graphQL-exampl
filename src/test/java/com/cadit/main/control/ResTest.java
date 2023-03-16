package com.cadit.main.control;

import org.junit.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;

public class ResTest {

    @Test
    public void testHelloMsg() throws IOException {
//        final Response response = given().get("/graphQL-example/app/res/getHelloMsg/Roby").then().assertThat().extract().response();
        final String response = given().get("/graphQL-example/app/res/getHelloMsg/Roby").then().assertThat().extract().response().asString();
        System.out.println(response);
    }
}
