package ru.fintech.qa.petshop;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.fintech.qa.models.pet.Pet;
import ru.fintech.qa.models.pet.Category;

import java.util.Arrays;
import java.util.stream.Stream;



public class StatusCodeTests {

    @BeforeEach
    public final void beforeEach() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
    }

    public static Stream<Arguments> params() {
        Pet myPet = new Pet();
        myPet.setId(9222968140498484534L);
        myPet.setName("Sunny");
        Category category = new Category();
        category.setName("Dog");
        myPet.setCategory(category);
        myPet.setStatus("available");

        Pet myPet1 = new Pet();
        myPet1.setId(-2);
        myPet1.setName("Sunny");
        Category category1 = new Category();
        category1.setName("Dog");
        myPet1.setCategory(category);
        myPet1.setStatus("available");

        Pet myPet3 = new Pet();
        myPet3.setId(2);
        myPet3.setName(null);
        Category category3 = new Category();
        category3.setName("Dog");
        myPet3.setCategory(category);
        myPet3.setStatus("available");

        return Arrays.asList(myPet, myPet1, myPet3).stream().map(pet -> Arguments.of(pet));
    }

    //POST myPet and receive status code 200
    @ParameterizedTest
    @MethodSource("params")
    public final void test1(final Pet myPet) {

        Pet restAssuredPet = RestAssured.given().contentType(ContentType.JSON)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .body(myPet)
                .post("/pet").then().assertThat().statusCode(200)
                .extract().as(Pet.class);

        Assertions.assertEquals(myPet, restAssuredPet);

    }

    //GET myPet and receive status code 200

    @ParameterizedTest
    @MethodSource("params")
    public final void test2(final Pet myPet) {

        RestAssured.given().contentType(ContentType.JSON)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .body(myPet)
                .post("/pet").then()
                .extract().as(Pet.class);

        Pet restAssuredPet = RestAssured.get("/pet/" + myPet.getId())
                .then().assertThat().statusCode(200)
                .log().all()
                .extract().as(Pet.class);
        Assertions.assertEquals(myPet, restAssuredPet);

    }

    //try GET myPet but receive status code 404, because we`ve deleted it firstly

    @ParameterizedTest
    @MethodSource("params")
    public final void test3(final Pet myPet) {

        RestAssured.given().contentType(ContentType.JSON)
                .body(myPet)
                .post("/pet");

        RestAssured.delete("/pet/" + myPet.getId());

        RestAssured.get("/pet/" + myPet.getId()).then().assertThat().statusCode(404).log().all();

    }

    //PUT to change myPet and receive changed myPet status code 200
    @ParameterizedTest
    @MethodSource("params")
    public final void test4(final Pet myPet) {

        RestAssured.given().contentType(ContentType.JSON)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .body(myPet)
                .post("/pet").then()
                .extract().as(Pet.class);

        myPet.setName("BigSunny"); // change the name


        RestAssured.given().contentType(ContentType.JSON)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .body(myPet)
                .put("/pet").then().assertThat().statusCode(200).log().all()
                .extract().as(Pet.class);

    }

    //try PUT to change myPet and receive status code 404, because we`ve deleted it firstly
    //but receive 200 because PUT creates new in case the pet doesn`t exist
    @ParameterizedTest
    @MethodSource("params")
    public final void test5(final Pet myPet) {

        RestAssured.given().contentType(ContentType.JSON)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .body(myPet)
                .post("/pet").then()
                .extract().as(Pet.class);

        RestAssured.delete("/pet/" + myPet.getId());

        myPet.setName("BigSunny"); // change the name


        RestAssured.given().contentType(ContentType.JSON)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .body(myPet)
                .put("/pet").then().assertThat().statusCode(404).log().all()
                .extract().as(Pet.class);
    }

    //DELETE to delete myPet and get status code 200

    @ParameterizedTest
    @MethodSource("params")
    public final void test6(final Pet myPet) {

        RestAssured.given().contentType(ContentType.JSON)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .body(myPet)
                .post("/pet").then()
                .extract().as(Pet.class);

        RestAssured.delete("/pet/" + myPet.getId()).then().assertThat().statusCode(200).log().all();

        RestAssured.get("/pet/" + myPet.getId()).then().assertThat().statusCode(404);
    }

//DELETE to delete unexisting Pet and get status code 404

    @Test
    public void test7() {
      // first to delete if pet with id 1 exists
        RestAssured.delete("/pet/1");

        // second try to delete pet with id 1, which must have been deleted already

        RestAssured.delete("/pet/1").then().assertThat().statusCode(404).log().all();
    }

}
