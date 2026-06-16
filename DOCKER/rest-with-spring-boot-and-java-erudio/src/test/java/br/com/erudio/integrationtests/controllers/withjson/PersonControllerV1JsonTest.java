package br.com.erudio.integrationtests.controllers.withjson;

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
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerV1JsonTest extends AbstractIntegrationTest {
    private static TokenDTO tokenDto;


    private static RequestSpecification specification;
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
    void createTest() throws JsonProcessingException {

        tokenDto = Authenticator.signin();
        specification = Specification.getSpecification(
                "/api/v1/person",
                TestConfigs.ORIGIN_LOCAL_BACKEND,
                tokenDto.getAccessToken()
        ).build();
        var content = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(person)
                .when()
                    .post()
                .then()
                .	statusCode(201)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                    .body()
                    .asString();

        PersonDTOV1 createdPerson = objectMapper.readValue(content, PersonDTOV1.class);
        person = createdPerson;

        assertNotNull(createdPerson.getId());
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
    void findByIdTest() throws JsonProcessingException {

        var content = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", person.getId())
                .when()
                .get("{id}")
                .then()
                .	statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .body()
                .asString();

        PersonDTOV1 createdPerson = objectMapper.readValue(content, PersonDTOV1.class);
        person = createdPerson;

        assertNotNull(createdPerson.getId());
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
    @Order(3)
    void updateTest() throws JsonProcessingException {

//        FIND LAST ID
        Long id = person.getId();

        var contentOfFindById = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", id)
                .when()
                .get("{id}")
                .then()
                .	statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .body()
                .asString();

        PersonDTOV1 personToBeAltered = objectMapper.readValue(contentOfFindById, PersonDTOV1.class);

//        SET USER
        personToBeAltered.setFirstName("Claudio");
        personToBeAltered.setLastName("Santoro");
        personToBeAltered.setAddress("Manaus, Ceará, Brazil");
        personToBeAltered.setGender("Male");
        personToBeAltered.setEnabled(true);
        personToBeAltered.setProfileUrl("http://profile.com/claudio_santoro");
        personToBeAltered.setPhotoUrl("http://photo.com/claudio_santoro.jpg");

        var contentOfUpdate = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(personToBeAltered)
                .when()
                .put()
                .then()
                .	statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
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
        assertEquals(personToBeAltered.getProfileUrl(), replacedPerson.getProfileUrl());
        assertEquals(personToBeAltered.getPhotoUrl(), replacedPerson.getPhotoUrl());
    }

    @Test
    @Order(4)
    void disableTest() throws JsonProcessingException {
        Long id = person.getId();

        var content = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", id)
                .when()
                .patch("{id}")
                .then()
                .	statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .body()
                .asString();

        PersonDTOV1 createdPerson = objectMapper.readValue(content, PersonDTOV1.class);
        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);

        assertEquals(person.getFirstName(), createdPerson.getFirstName());
        assertEquals(person.getLastName(), createdPerson.getLastName());
        assertEquals(person.getAddress(), createdPerson.getAddress());
        assertEquals(person.getGender(), createdPerson.getGender());
        assertFalse(createdPerson.getEnabled());
        assertEquals(person.getProfileUrl(), createdPerson.getProfileUrl());
        assertEquals(person.getPhotoUrl(), createdPerson.getPhotoUrl());
    }

    @Test
    @Order(5)
    void deleteTest() throws JsonProcessingException {
        Long id = person.getId();

       given(specification)
                .pathParam("id", id)
                .when()
                .delete("{id}")
                .then()
                .	statusCode(204);
    }

    @Test
    @Order(6)
    void findAllTest() throws JsonProcessingException {

        var content = given(specification)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParams("page", 3, "size", 12, "direction", "asc")
                .when()
                .get()
                .then()
                .	statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .body()
                .asString();

        WrapperPersonDTO wrapper = objectMapper.readValue(content, WrapperPersonDTO.class);
        List<PersonDTOV1> people = wrapper.getEmbedded().getPeople();

        PersonDTOV1 personOne = people.get(0);

        assertNotNull(personOne.getId());
        assertTrue(personOne.getId() > 0);

        assertEquals("Allin", personOne.getFirstName());
        assertEquals("Emmot", personOne.getLastName());
        assertEquals("7913 Lindbergh Way", personOne.getAddress());
        assertEquals("Male", personOne.getGender());
        assertFalse(personOne.getEnabled());
        assertEquals("https://pub.erudio.com.br/meus-cursos", personOne.getProfileUrl());
        assertEquals("https://pub.erudio.com.br/meus-cursos", personOne.getProfileUrl());


        PersonDTOV1 personFour = people.get(3);

        assertNotNull(personFour.getId());
        assertTrue(personFour.getId() > 0);

        assertEquals("Almeria", personFour.getFirstName());
        assertEquals("Curm", personFour.getLastName());
        assertEquals("34 Burrows Point", personFour.getAddress());
        assertEquals("Female", personFour.getGender());
        assertFalse(personFour.getEnabled());
        assertEquals("https://pub.erudio.com.br/meus-cursos", personFour.getProfileUrl());
        assertEquals("https://raw.githubusercontent.com/leandrocgsi/rest-with-spring-boot-and-java-erudio/refs/heads/main/photos/00_some_person.jpg", personFour.getPhotoUrl());
    }

    @Order(7)
    @Test
    void findByNameTest() throws JsonProcessingException {

        var content = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParams("firstName", "and")
                .queryParams("page", 0, "size", 10, "direction", "asc")
                .when()
                .get("findPeopleByName/{firstName}")
                .then()
                .	statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .body()
                .asString();

        WrapperPersonDTO wrapper = objectMapper.readValue(content, WrapperPersonDTO.class);
        List<PersonDTOV1> people = wrapper.getEmbedded().getPeople();

        PersonDTOV1 personOne = people.get(0);

        assertNotNull(personOne.getId());
        assertTrue(personOne.getId() > 0);

        assertEquals("Alessandro", personOne.getFirstName());
        assertEquals("McFaul", personOne.getLastName());
        assertEquals("5 Lukken Plaza", personOne.getAddress());
        assertEquals("Male", personOne.getGender());
        assertTrue(personOne.getEnabled());
        assertEquals("https://pub.erudio.com.br/meus-cursos", personOne.getProfileUrl());
        assertEquals("https://raw.githubusercontent.com/leandrocgsi/rest-with-spring-boot-and-java-erudio/refs/heads/main/photos/00_some_person.jpg", personOne.getPhotoUrl());


        PersonDTOV1 personFour = people.get(3);

        assertNotNull(personFour.getId());
        assertTrue(personFour.getId() > 0);

        assertEquals("Brander", personFour.getFirstName());
        assertEquals("Besnardeau", personFour.getLastName());
        assertEquals("81352 Melby Lane", personFour.getAddress());
        assertEquals("Male", personFour.getGender());
        assertFalse(personFour.getEnabled());
        assertEquals("https://pub.erudio.com.br/meus-cursos", personFour.getProfileUrl());
        assertEquals("https://raw.githubusercontent.com/leandrocgsi/rest-with-spring-boot-and-java-erudio/refs/heads/main/photos/00_some_person.jpg", personFour.getPhotoUrl());
    }
}
