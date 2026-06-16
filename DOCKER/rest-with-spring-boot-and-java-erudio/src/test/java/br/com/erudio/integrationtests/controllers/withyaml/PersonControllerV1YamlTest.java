package br.com.erudio.integrationtests.controllers.withyaml;

import br.com.erudio.config.TestConfigs;
import br.com.erudio.integrationtests.controllers.withyaml.mapper.YAMLMapper;
import br.com.erudio.integrationtests.dto.PersonDTOV1;
import br.com.erudio.integrationtests.dto.security.TokenDTO;
import br.com.erudio.integrationtests.dto.wrappers.yaml.PagedModelPersonDTO;
import br.com.erudio.integrationtests.mocks.PersonMock;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.utils.auth.Authenticator;
import br.com.erudio.utils.specification.Specification;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerV1YamlTest extends AbstractIntegrationTest {
    private static TokenDTO tokenDto;

    private static RequestSpecification specification;
    private static YAMLMapper objectMapper;
    private static PersonDTOV1 person;

    @BeforeAll
    static void setUp() {
        tokenDto = new TokenDTO();

        objectMapper = new YAMLMapper();

        person = new PersonDTOV1();
        person = PersonMock.mockPerson("Male");

    }

    @Test
    @Order(1)
    void createTest() throws JsonProcessingException {
        tokenDto = Authenticator.signin();
        specification = Specification.getSpecificationWithYaml(
                "/api/v1/person",
                TestConfigs.ORIGIN_LOCAL_BACKEND,
                tokenDto.getAccessToken()
        ).build();
        var content = given(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .body(person, objectMapper)
                .when()
                    .post()
                .then()
                .statusCode(201)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                    .body()
                    .as(PersonDTOV1.class, objectMapper);

        person = content;
        assertNotNull(content.getId());
        assertTrue(content.getId() > 0);

        assertEquals(person.getFirstName(), content.getFirstName());
        assertEquals(person.getLastName(), content.getLastName());
        assertEquals(person.getAddress(), content.getAddress());
        assertEquals(person.getGender(), content.getGender());
        assertEquals(person.getEnabled(), content.getEnabled());
        assertEquals(person.getProfileUrl(), content.getProfileUrl());
        assertEquals(person.getPhotoUrl(), content.getPhotoUrl());
    }

    @Test
    @Order(2)
    void findByIdTest() throws JsonProcessingException {

        var foundPerson = given(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .pathParam("id", person.getId())
                .when()
                .get("{id}")
                .then()
                .	statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PersonDTOV1.class, objectMapper);

        assertNotNull(foundPerson.getId());
        assertTrue(foundPerson.getId() > 0);

        assertEquals(person.getFirstName(), foundPerson.getFirstName());
        assertEquals(person.getLastName(), foundPerson.getLastName());
        assertEquals(person.getAddress(), foundPerson.getAddress());
        assertEquals(person.getGender(), foundPerson.getGender());
        assertEquals(person.getEnabled(), foundPerson.getEnabled());
        assertEquals(person.getProfileUrl(), foundPerson.getProfileUrl());
        assertEquals(person.getPhotoUrl(), foundPerson.getPhotoUrl());
    }

    @Test
    @Order(3)
    void updateTest() throws JsonProcessingException {

//        FIND LAST ID
        Long id = person.getId();

        var foundPerson = given(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .pathParam("id", id)
                .when()
                .get("{id}")
                .then()
                .	statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PersonDTOV1.class, objectMapper);

        PersonDTOV1 personToBeAltered = new PersonDTOV1();

//        SET USER
        personToBeAltered.setId(foundPerson.getId());
        personToBeAltered.setFirstName("Claudio");
        personToBeAltered.setLastName("Santoro");
        personToBeAltered.setAddress("Manaus, Ceará, Brazil");
        personToBeAltered.setGender("Male");
        personToBeAltered.setEnabled(true);
        personToBeAltered.setProfileUrl("https://claudiosantoro.com");
        personToBeAltered.setPhotoUrl("https://claudiosantoro.com/photo.jpg");

        var replacedPerson = given(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .body(personToBeAltered, objectMapper)
                .when()
                .put()
                .then()
                .	statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PersonDTOV1.class, objectMapper);

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

        person = replacedPerson;
    }

    @Test
    @Order(4)
    void disableTest() throws JsonProcessingException {
        Long id = person.getId();

        var disabledPerson = given(specification)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .pathParam("id", id)
                .when()
                .patch("{id}")
                .then()
                .	statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PersonDTOV1.class, objectMapper);

        assertNotNull(disabledPerson.getId());
        assertTrue(disabledPerson.getId() > 0);

        assertEquals(person.getFirstName(), disabledPerson.getFirstName());
        assertEquals(person.getLastName(), disabledPerson.getLastName());
        assertEquals(person.getAddress(), disabledPerson.getAddress());
        assertEquals(person.getGender(), disabledPerson.getGender());
        assertFalse(disabledPerson.getEnabled());
        assertEquals(person.getProfileUrl(), disabledPerson.getProfileUrl());
        assertEquals(person.getPhotoUrl(), disabledPerson.getPhotoUrl());
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

        var response = given(specification)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .queryParams("page", 5, "size", 4, "direction", "asc")
                .when()
                .get()
                .then()
                .	statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PagedModelPersonDTO.class, objectMapper);

        List<PersonDTOV1> people = response.getContent();

        PersonDTOV1 personOne = people.getFirst();

        assertNotNull(personOne.getId());
        assertTrue(personOne.getId() > 0);

        assertEquals("Alberto", personOne.getFirstName());
        assertEquals("Chazotte", personOne.getLastName());
        assertEquals("384 Maple Place", personOne.getAddress());
        assertEquals("Male", personOne.getGender());
        assertFalse(personOne.getEnabled());
        assertEquals("https://pub.erudio.com.br/meus-cursos", personOne.getProfileUrl());
        assertEquals("https://raw.githubusercontent.com/leandrocgsi/rest-with-spring-boot-and-java-erudio/refs/heads/main/photos/00_some_person.jpg", personOne.getPhotoUrl());

        PersonDTOV1 personFour = people.get(3);

        assertNotNull(personFour.getId());
        assertTrue(personFour.getId() > 0);

        assertEquals("Aldrich", personFour.getFirstName());
        assertEquals("Izkovicz", personFour.getLastName());
        assertEquals("62 Northview Trail", personFour.getAddress());
        assertEquals("Male", personFour.getGender());
        assertTrue(personFour.getEnabled());
        assertEquals("https://pub.erudio.com.br/meus-cursos", personFour.getProfileUrl());
        assertEquals("https://raw.githubusercontent.com/leandrocgsi/rest-with-spring-boot-and-java-erudio/refs/heads/main/photos/00_some_person.jpg", personFour.getPhotoUrl());
    }

    @Test
    @Order(7)
    void findByNameTest() throws JsonProcessingException {

        var response = given(specification)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .pathParams("firstName", "and")
                .queryParams("page", 0, "size", 10, "direction", "asc")
                .when()
                .get("findPeopleByName/{firstName}")
                .then()
                .	statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PagedModelPersonDTO.class, objectMapper);

        List<PersonDTOV1> people = response.getContent();

        PersonDTOV1 personOne = people.getFirst();

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
