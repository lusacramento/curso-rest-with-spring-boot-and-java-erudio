package br.com.erudio.integrationtests.controllers.cors.withjson;

import br.com.erudio.config.TestConfigs;
import br.com.erudio.integrationtests.dto.BookDTO;
import br.com.erudio.integrationtests.dto.security.TokenDTO;
import br.com.erudio.integrationtests.dto.wrappers.json.WrapperBookDTO;
import br.com.erudio.integrationtests.mocks.BookMock;
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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerV1CorsTest extends AbstractIntegrationTest {
    private static TokenDTO tokenDto;

    private static RequestSpecification validOriginSpecification;
    private static RequestSpecification wrongOriginSpecification;
    private static ObjectMapper objectMapper;
    private static BookDTO book;

    @BeforeAll
    static void setUp() {
        tokenDto = new TokenDTO();

        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        book = new BookDTO();
        book = BookMock.mockBook();
    }

    @Test
    @Order(1)
    void create() throws JsonProcessingException {
        tokenDto = Authenticator.signin();
        tokenDto = Authenticator.signin();
        validOriginSpecification = Specification.getSpecification(
                "/api/book",
                TestConfigs.ORIGIN_LOCAL_BACKEND,
                tokenDto.getAccessToken()
        ).build();
        var content = given(validOriginSpecification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(book)
                .when()
                    .post()
                .then()
                .	statusCode(201)
                .extract()
                    .body()
                    .asString();

        BookDTO createdPerson = objectMapper.readValue(content, BookDTO.class);
        book = createdPerson;

        assertNotNull(createdPerson.getId());
        assertNotNull(createdPerson.getAuthor());
        assertNotNull(createdPerson.getLaunchDate());
        assertNotNull(createdPerson.getPrice());
        assertNotNull(createdPerson.getTitle());

        assertTrue(createdPerson.getId() > 0);

        assertEquals(book.getAuthor(), createdPerson.getAuthor());
        assertEquals(book.getLaunchDate(), createdPerson.getLaunchDate());
        assertEquals(book.getPrice(), createdPerson.getPrice());
        assertEquals(book.getTitle(), createdPerson.getTitle());
    }

    @Test
    @Order(2)
    void createWithWrongOrigin() throws JsonProcessingException {
        wrongOriginSpecification = Specification.getSpecification(
                "/api/book",
                TestConfigs.ORIGIN_NOT_ALLOWED,
                tokenDto.getAccessToken()
        ).build();
        var content = given(wrongOriginSpecification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(book)
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
                .pathParam("id", book.getId())
                .when()
                .get("{id}")
                .then()
                .	statusCode(200)
                .extract()
                .body()
                .asString();

        BookDTO createdPerson = objectMapper.readValue(content, BookDTO.class);
        book = createdPerson;

        assertNotNull(createdPerson.getId());
        assertNotNull(createdPerson.getAuthor());
        assertNotNull(createdPerson.getLaunchDate());
        assertNotNull(createdPerson.getPrice());
        assertNotNull(createdPerson.getTitle());

        assertTrue(createdPerson.getId() > 0);

        assertEquals(book.getAuthor(), createdPerson.getAuthor());
        assertEquals(book.getLaunchDate(), createdPerson.getLaunchDate());
        assertEquals(book.getPrice(), createdPerson.getPrice());
        assertEquals(book.getTitle(), createdPerson.getTitle());
    }

    @Test
    @Order(4)
    void findByIdWithWrongOrigin() throws JsonProcessingException {

        var content = given(wrongOriginSpecification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", book.getId())
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
        book = BookMock.mockBook();
        create();

        var content = given(validOriginSpecification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParams("page", 1, "size", 4, "direction", "asc")
                .when()
                .get()
                .then()
                .	statusCode(200)
                .extract()
                .body()
                .asString();

        WrapperBookDTO wrapper = objectMapper.readValue(content, WrapperBookDTO.class);
        List<BookDTO> books = wrapper.getEmbedded().getBooks();

        assertFalse(books.isEmpty()); // there are more or equals 2 books on database

        BookDTO firstBook = books.getFirst();
        assertNotNull(firstBook.getId());
        assertEquals("Craig Larman", firstBook.getAuthor());
        assertEquals("1998-12-05 00:00:00", firstBook.getLaunchDate());
        assertEquals(43.82, firstBook.getPrice());
        assertEquals("Agile and Iterative Development: A Manager’s Guide", firstBook.getTitle());

        BookDTO fourthBook = books.get(3);
        assertNotNull(fourthBook.getId());
        assertEquals("Mike Cohn", fourthBook.getAuthor());
        assertEquals("2011-12-09 00:00:00", fourthBook.getLaunchDate());
        assertEquals(112.87, fourthBook.getPrice());
        assertEquals("Agile Estimating and Planning", fourthBook.getTitle());
    }

    @Test
    @Order(6)
    void findAllWithWrongOrigin() throws JsonProcessingException {
        book = BookMock.mockBook();
        create();

        var content = given(wrongOriginSpecification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParams("page", 0, "size", 4, "direction", "asc")
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
        Long id = book.getId();

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

        BookDTO bookToBeAltered = objectMapper.readValue(contentOfFindById, BookDTO.class);

//        SET USER
        bookToBeAltered.setAuthor("Morgan Housel");
        bookToBeAltered.setLaunchDate("2017-11-29 13:50:05.878000");
        bookToBeAltered.setPrice(20.0);
        bookToBeAltered.setTitle("A Psicologia Financeira");

        var contentOfUpdate = given(validOriginSpecification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(bookToBeAltered)
                .when()
                .put()
                .then()
                .	statusCode(200) // Expected 200. Return 405
                .extract()
                .body()
                .asString();

        BookDTO replacedPerson = objectMapper.readValue(contentOfUpdate, BookDTO.class);

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
    }

    @Test
    @Order(8)
    void updateWithWrongOrigin() throws JsonProcessingException {

//        FIND LAST ID
        Long id = book.getId();

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

        assertEquals("Invalid CORS request", content);
    }

    @Test
    @Order(9)
    void delete() {
        Long id = book.getId();

        var content = given(validOriginSpecification)
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
        Long id = book.getId();

        var content = given(wrongOriginSpecification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", id)
                .when()
                .delete("{id}")
                .then()
                .	statusCode(403);
    }
}
