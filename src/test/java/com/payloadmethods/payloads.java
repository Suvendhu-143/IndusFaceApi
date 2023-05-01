package com.payloadmethods;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class payloads {

    public static Response post(Object payloads,String url )
    {
        Response response = RestAssured.given().body(payloads).contentType(ContentType.JSON).log().all()
                .when().post(url)
                .then().log().all().extract().response();
        return response;

    }
    public static Response post(Object payloads,String url,String header)
    {
        Response response = RestAssured.given().body(payloads).header("Authorization", header).contentType(ContentType.JSON).log().all()
                .when().post(url)
                .then().log().all().extract().response();
        return response;

    }

    public static Response get(String url,String header)
    {
        Response response = RestAssured.given().header("Authorization", header).contentType(ContentType.JSON).log().all()
                .when().get(url)
                .then().log().all().extract().response();
        return response;

    }
    public static Response get(String url,String header,String path)
    {
        Response response = RestAssured.given().basePath(path).header("Authorization", header).contentType(ContentType.JSON).log().all()
                .when().get(url)
                .then().log().all().extract().response();
        return response;

    }

}
