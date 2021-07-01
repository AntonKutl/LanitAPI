package org.example.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.example.model.Order;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import java.io.IOException;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class HomeTaskApiTest {
    private Order order = new Order();


    @BeforeClass
    public void prepare() throws IOException {
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io/v2/")
                .addHeader("api_key", System.getProperty("api.key"))
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();

        RestAssured.filters(new ResponseLoggingFilter());
    }

    @Test
    public void createOrder() {
        order.setId(new Random().nextInt(10));
        order.setPetId(new Random().nextInt(10));
        Order actual =
                given().
                        body(order)
                        .when()
                        .post("/store/order")
                        .then().statusCode(200)
                        .extract().body()
                        .as(Order.class);
        Assert.assertEquals(order.getId(), actual.getId());
    }

    @Test
    public void returnsStatus() {
        Map map =
                given()
                        .when()
                        .get("/store/inventory")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(Map.class);
        Assert.assertTrue(map.containsKey("sold"), "Inventory не содержит статус sold");


    }

    @Test
    public void deleteOrder() {
        given()
                .pathParams("orderId", order.getId())
                .when()
                .delete("/store/order/{orderId}")
                .then()
                .statusCode(200);
    }

    @Test
    public void findOrder() {
        given()
                .pathParams("petId", order.getPetId())
                .when()
                .get("/store/order/{petId}")
                .then()
                .statusCode(200);
    }

}
