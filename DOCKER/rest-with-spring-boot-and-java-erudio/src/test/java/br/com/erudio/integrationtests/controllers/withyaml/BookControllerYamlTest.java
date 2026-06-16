package br.com.erudio.integrationtests.controllers.withyaml;

import br.com.erudio.config.TestConfigs;
import br.com.erudio.integrationtests.controllers.withyaml.mapper.YAMLMapper;
import br.com.erudio.integrationtests.dto.BookDTO;
import br.com.erudio.integrationtests.dto.security.TokenDTO;
import br.com.erudio.integrationtests.dto.wrappers.yaml.PagedModelBookDTO;
import br.com.erudio.integrationtests.mocks.BookMock;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.utils.auth.Authenticator;
import br.com.erudio.utils.specification.Specification;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerYamlTest extends AbstractIntegrationTest {
    private static TokenDTO tokenDto;

    private static RequestSpecification specification;
    private static YAMLMapper objectMapper;
    private static BookDTO book;

    @BeforeAll
    static void setUp() {
        tokenDto = new TokenDTO();

        objectMapper = new YAMLMapper();

        book = new BookDTO();
        book = BookMock.mockBook();

    }

    @Test
    @Order(1)
    void createTest() throws JsonProcessingException {
        tokenDto = Authenticator.signin();
        specification = Specification.getSpecificationWithYaml(
                "/api/book",
                TestConfigs.ORIGIN_LOCAL_BACKEND,
                tokenDto.getAccessToken()
        ).build();
        var content = given(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .body(book, objectMapper)
                .when()
                .post()
                .then()
                .statusCode(201)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(BookDTO.class, objectMapper);

        book = content;
        assertNotNull(content.getId());
        assertTrue(content.getId() > 0);

        assertEquals(book.getAuthor(), content.getAuthor());
        assertEquals(book.getLaunchDate(), content.getLaunchDate());
        assertEquals(book.getPrice(), content.getPrice());
        assertEquals(book.getTitle(), content.getTitle());
    }

    @Test
    @Order(2)
    void findByIdTest() throws JsonProcessingException {

        var foundPerson = given(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .pathParam("id", book.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(BookDTO.class, objectMapper);

        assertNotNull(foundPerson.getId());
        assertTrue(foundPerson.getId() > 0);

        assertEquals(book.getAuthor(), foundPerson.getAuthor());
        assertEquals(book.getLaunchDate(), foundPerson.getLaunchDate());
        assertEquals(book.getPrice(), foundPerson.getPrice());
        assertEquals(book.getTitle(), foundPerson.getTitle());
    }

    @Test
    @Order(3)
    void updateTest() throws JsonProcessingException {

//        FIND LAST ID
        Long id = book.getId();

        var foundPerson = given(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .pathParam("id", id)
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(BookDTO.class, objectMapper);

        BookDTO bookToBeAltered = new BookDTO();

//        SET USER
        bookToBeAltered.setId(foundPerson.getId());
        bookToBeAltered.setAuthor("Claudio");
        bookToBeAltered.setLaunchDate("2025-11-29 13:50:05.878000");
        bookToBeAltered.setPrice(33.0);
        bookToBeAltered.setTitle("Male");

        var replacedPerson = given(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .body(bookToBeAltered, objectMapper)
                .when()
                .put()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(BookDTO.class, objectMapper);

        // Assertations
        assertNotNull(replacedPerson.getId());
        assertNotNull(replacedPerson.getAuthor());
        assertNotNull(replacedPerson.getLaunchDate());
        assertNotNull(replacedPerson.getPrice());
        assertNotNull(replacedPerson.getTitle());

        assertTrue(replacedPerson.getId() > 0);

        assertEquals(bookToBeAltered.getAuthor(), replacedPerson.getAuthor());
        assertEquals(bookToBeAltered.getLaunchDate(), replacedPerson.getLaunchDate());
        assertEquals(bookToBeAltered.getPrice(), replacedPerson.getPrice());
        assertEquals(bookToBeAltered.getTitle(), replacedPerson.getTitle());

        book = replacedPerson;
    }

    @Test
    @Order(4)
    void deleteTest() throws JsonProcessingException {
        Long id = book.getId();

        given(specification)
                .pathParam("id", id)
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(5)
    void findAllTest() throws JsonProcessingException {

        var response = given(specification)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .queryParams("page", 0, "size", 4, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PagedModelBookDTO.class, objectMapper);

        List<BookDTO> book = response.getContent();

        BookDTO bookOne = book.getFirst();

        assertNotNull(bookOne.getId());
        assertTrue(bookOne.getId() > 0);

        assertEquals("Craig Larman", bookOne.getAuthor());
        assertEquals("2018-08-14 00:00:00", bookOne.getLaunchDate());
        assertEquals(72.89, bookOne.getPrice());
        assertEquals("Agile and Iterative Development: A Manager’s Guide", bookOne.getTitle());

        BookDTO bookFour = book.get(3);

        assertNotNull(bookFour.getId());
        assertTrue(bookFour.getId() > 0);

        assertEquals("Craig Larman", bookFour.getAuthor());
        assertEquals("1998-12-05 00:00:00", bookFour.getLaunchDate());
        assertEquals(43.82, bookFour.getPrice());
        assertEquals("Agile and Iterative Development: A Manager’s Guide", bookFour.getTitle());
    }
}
