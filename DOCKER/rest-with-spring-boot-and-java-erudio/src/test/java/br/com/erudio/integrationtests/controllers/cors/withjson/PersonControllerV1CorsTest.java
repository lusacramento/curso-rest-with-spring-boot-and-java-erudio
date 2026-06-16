package br.com.erudio.integrationtests.controllers.cors.withjson;

import br.com.erudio.config.TestConfigs;
import br.com.erudio.integrationtests.dto.PersonDTOV1;
import br.com.erudio.integrationtests.dto.security.TokenDTO;
import br.com.erudio.integrationtests.dto.wrappers.json.WrapperPersonDTO;
import br.com.erudio.integrationtests.mocks.PersonMock;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.utils.auth.Authenticator;
import br.com.erudio.utils.specification.Specification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerV1CorsTest extends AbstractIntegrationTest {
    private static TokenDTO tokenDto;

    private static RequestSpecification validOriginSpecification;
    private static RequestSpecification wrongOriginSpecification;
    private static ObjectMapper objectMapper;
    private static PersonDTOV1 person;

    @BeforeAll
    static void setUp() {
        tokenDto = new TokenDTO();

        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        person = new PersonDTOV1();
        person = PersonMock.mockPerson("Male");
    }

    @Test
    @Order(1)
    void create() throws JsonProcessingException {

        tokenDto = Authenticator.signin();
        validOriginSpecification = Specification.getSpecification(
                "/api/v1/person",
                TestConfigs.ORIGIN_LOCAL_BACKEND,
                tokenDto.getAccessToken()
        ).build();
        var content = given(validOriginSpecification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(person)
                .when()
                    .post()
                .then()
                .	statusCode(201)
                .extract()
                    .body()
                    .asString();

        PersonDTOV1 createdPerson = objectMapper.readValue(content, PersonDTOV1.class);
        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertNotNull(createdPerson.getFirstName());
        assertNotNull(createdPerson.getLastName());
        assertNotNull(createdPerson.getAddress());
        assertNotNull(createdPerson.getGender());
        assertNotNull(createdPerson.getEnabled());
        assertNotNull(createdPerson.getProfileUrl());
        assertNotNull(createdPerson.getPhotoUrl());

        assertTrue(createdPerson.getId() > 0);

        assertEquals(person.getFirstName(), createdPerson.getFirstName());
        assertEquals(person.getLastName(), createdPerson.getLastName());
        assertEquals(person.getAddress(), createdPerson.getAddress());
        assertEquals(person.getGender(), createdPerson.getGender());
        assertEquals(person.getEnabled(), createdPerson.getEnabled());
        assertEquals(person.getProfileUrl(), createdPerson.getProfileUrl());
        assertEquals(person.getPhotoUrl(), createdPerson.getPhotoUrl());
    }

    @Test
    @Order(2)
    void createWithWrongOrigin() throws JsonProcessingException {

        wrongOriginSpecification = Specification.getSpecification(
                "/api/v1/person",
                TestConfigs.ORIGIN_NOT_ALLOWED,
                tokenDto.getAccessToken()
        ).build();
        var content = given(wrongOriginSpecification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(person)
                .when()
                .post()
                .then()
                .	statusCode(403)
                .extract()
                .body()
                .asString();

        assertEquals("Invalid CORS request", content);
    }

    @Test
    @Order(3)
    void findById() throws JsonProcessingException {

        var content = given(validOriginSpecification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", person.getId())
                .when()
                .get("{id}")
                .then()
                .	statusCode(200)
                .extract()
                .body()
                .asString();

        PersonDTOV1 createdPerson = objectMapper.readValue(content, PersonDTOV1.class);
        person = createdPerson;assertEquals(person.getProfileUrl(), createdPerson.getProfileUrl());
        assertEquals(person.getPhotoUrl(), createdPerson.getPhotoUrl());

        assertNotNull(createdPerson.getId());
        assertNotNull(createdPerson.getFirstName());
        assertNotNull(createdPerson.getLastName());
        assertNotNull(createdPerson.getAddress());
        assertNotNull(createdPerson.getGender());
        assertNotNull(createdPerson.getEnabled());
        assertNotNull(createdPerson.getProfileUrl());
        assertNotNull(createdPerson.getPhotoUrl());

        assertTrue(createdPerson.getId() > 0);

        assertEquals(person.getFirstName(), createdPerson.getFirstName());
        assertEquals(person.getLastName(), createdPerson.getLastName());
        assertEquals(person.getAddress(), createdPerson.getAddress());
        assertEquals(person.getGender(), createdPerson.getGender());
        assertEquals(person.getEnabled(), createdPerson.getEnabled());
        assertEquals(person.getProfileUrl(), createdPerson.getProfileUrl());
        assertEquals(person.getPhotoUrl(), createdPerson.getPhotoUrl());
    }

    @Test
    @Order(4)
    void findByIdWithWrongOrigin() throws JsonProcessingException {

        var content = given(wrongOriginSpecification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", person.getId())
                .when()
                .get("{id}")
                .then()
                .	statusCode(403)
                .extract()
                .body()
                .asString();

        assertEquals("Invalid CORS request", content);
    }

    @Test
    @Order(5)
    void findAll() throws JsonProcessingException {
        person = PersonMock.mockPerson("Female");
        create();

        var content = given(validOriginSpecification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParams("page", 5, "size", 4, "direction", "asc")
                .when()
                .get()
                .then()
                .	statusCode(200)
                .extract()
                .body()
                .asString();

        WrapperPersonDTO wrapper = objectMapper.readValue(content, WrapperPersonDTO.class);
        List<PersonDTOV1> people = wrapper.getEmbedded().getPeople();

        assertFalse(people.isEmpty()); // there are more or equals 2 persons on database

        PersonDTOV1 firstPerson = people.getFirst();
        assertNotNull(firstPerson.getId());
        assertEquals("Alberto", firstPerson.getFirstName());
        assertEquals("Chazotte", firstPerson.getLastName());
        assertEquals("384 Maple Place", firstPerson.getAddress());
        assertEquals("Male", firstPerson.getGender());
        assertFalse(firstPerson.getEnabled());
        assertEquals("https://pub.erudio.com.br/meus-cursos", firstPerson.getProfileUrl());
        assertEquals("https://raw.githubusercontent.com/leandrocgsi/rest-with-spring-boot-and-java-erudio/refs/heads/main/photos/00_some_person.jpg", firstPerson.getPhotoUrl());

        PersonDTOV1 fourthPerson = people.get(3);
        assertNotNull(fourthPerson.getId());
        assertEquals("Aldrich", fourthPerson.getFirstName());
        assertEquals("Izkovicz", fourthPerson.getLastName());
        assertEquals("62 Northview Trail", fourthPerson.getAddress());
        assertEquals("Male", fourthPerson.getGender());
        assertTrue(fourthPerson.getEnabled());
        assertEquals("https://pub.erudio.com.br/meus-cursos", firstPerson.getProfileUrl());
        assertEquals("https://raw.githubusercontent.com/leandrocgsi/rest-with-spring-boot-and-java-erudio/refs/heads/main/photos/00_some_person.jpg", firstPerson.getPhotoUrl());
    }

    @Test
    @Order(6)
    void findAllWithWrongOrigin() throws JsonProcessingException {
        person = PersonMock.mockPerson("Female");
        create();

        var content = given(wrongOriginSpecification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParams("page", 5, "size", 4, "direction", "asc")
                .when()
                .get()
                .then()
                .	statusCode(403)
                .extract()
                .body()
                .asString();

        assertEquals("Invalid CORS request", content);
    }

    @Test
    @Order(7)
    void update() throws JsonProcessingException {

//        FIND LAST ID
        Long id = person.getId();

        var contentOfFindById = given(validOriginSpecification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", id)
                .when()
                .get("{id}")
                .then()
                .	statusCode(200)
                .extract()
                .body()
                .asString();

        PersonDTOV1 personToBeAltered = objectMapper.readValue(contentOfFindById, PersonDTOV1.class);

//        SET USER
        personToBeAltered.setFirstName("Wesley");
        personToBeAltered.setLastName("Safadão");
        personToBeAltered.setAddress("Fortaleza, Ceará, Brazil");
        personToBeAltered.setGender("Male");
        personToBeAltered.setEnabled(true);
        personToBeAltered.setProfileUrl("https://pt.wikipedia.org/wiki/Wesley_Safad%C3%A3o");
        personToBeAltered.setPhotoUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/e/e3/Wesley_Safad%C3%A3o_Citibank_Hall_2017_%2836674588052%29_%28cropped%29.jpg/250px-Wesley_Safad%C3%A3o_Citibank_Hall_2017_%2836674588052%29_%28cropped%29.jpg");

        var contentOfUpdate = given(validOriginSpecification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(personToBeAltered)
                .when()
                .put()
                .then()
                .	statusCode(200) // Expected 200. Return 405
                .extract()
                .body()
                .asString();

        PersonDTOV1 replacedPerson = objectMapper.readValue(contentOfUpdate, PersonDTOV1.class);

        // Assertations
        assertNotNull(replacedPerson.getId());
        assertNotNull(replacedPerson.getFirstName());
        assertNotNull(replacedPerson.getLastName());
        assertNotNull(replacedPerson.getAddress());
        assertNotNull(replacedPerson.getGender());
        assertNotNull(replacedPerson.getEnabled());
        assertNotNull(replacedPerson.getProfileUrl());
        assertNotNull(replacedPerson.getPhotoUrl());

        assertTrue(replacedPerson.getId() > 0);

        assertEquals(personToBeAltered.getFirstName(), replacedPerson.getFirstName());
        assertEquals(personToBeAltered.getLastName(), replacedPerson.getLastName());
        assertEquals(personToBeAltered.getAddress(), replacedPerson.getAddress());
        assertEquals(personToBeAltered.getGender(), replacedPerson.getGender());
        assertEquals(personToBeAltered.getEnabled(), replacedPerson.getEnabled());
        assertEquals(personToBeAltered.getProfileUrl(), replacedPerson.getProfileUrl());
        assertEquals(personToBeAltered.getPhotoUrl(), replacedPerson.getPhotoUrl());
    }

    @Test
    @Order(8)
    void updateWithWrongOrigin() throws JsonProcessingException {

//        FIND LAST ID
        Long id = person.getId();

        var content = given(wrongOriginSpecification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", id)
                .when()
                .get("{id}")
                .then()
                .	statusCode(403)
                .extract()
                .body()
                .asString();
https://pt.wikipedia.org/wiki/Wesley_Safad%C3%A3o
        assertEquals("Invalid CORS request", content);
    }

    @Test
    @Order(9)
    void delete() {
        Long id = person.getId();

        given(validOriginSpecification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", id)
                .when()
                .delete("{id}")
                .then()
                .	statusCode(204);
    }

    @Test
    @Order(10)
    void deleteWithWrongOrigin() {
        Long id = person.getId();

        given(wrongOriginSpecification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", id)
                .when()
                .delete("{id}")
                .then()
                .	statusCode(403);
    }
}
