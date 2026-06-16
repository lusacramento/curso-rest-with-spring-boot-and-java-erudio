package br.com.erudio.integrationtests.controllers.withjson;


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
public class BookControllerJsonTest extends AbstractIntegrationTest {
    private static TokenDTO tokenDto;

    private static ObjectMapper objectMapper;
    private static BookDTO book;
    private static RequestSpecification specification;

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
    void createTest() throws JsonProcessingException {
        tokenDto = Authenticator.signin();
        specification = Specification.getSpecification(
                "/api/book",
                TestConfigs.ORIGIN_LOCAL_BACKEND,
                tokenDto.getAccessToken()
        ).build();
        var content = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(book)
                .when()
                .post()
                .then()
                .	statusCode(201)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .body()
                .asString();

        BookDTO createdBook = objectMapper.readValue(content, BookDTO.class);
        book = createdBook;

        assertNotNull(createdBook.getId());
        assertTrue(createdBook.getId() > 0);

        assertEquals(book.getAuthor(), createdBook.getAuthor());
        assertEquals(book.getLaunchDate(), createdBook.getLaunchDate());
        assertEquals(book.getPrice(), createdBook.getPrice());
        assertEquals(book.getTitle(), createdBook.getTitle());
    }

    @Test
    @Order(2)
    void findByIdTest() throws JsonProcessingException {

        var content = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", book.getId())
                .when()
                .get("{id}")
                .then()
                .	statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .body()
                .asString();

        BookDTO createdBook = objectMapper.readValue(content, BookDTO.class);
        book = createdBook;

        assertNotNull(createdBook.getId());
        assertTrue(createdBook.getId() > 0);

        assertEquals(book.getAuthor(), createdBook.getAuthor());
        assertEquals(book.getLaunchDate(), createdBook.getLaunchDate());
        assertEquals(book.getPrice(), createdBook.getPrice());
        assertEquals(book.getTitle(), createdBook.getTitle());
    }

    @Test
    @Order(3)
    void updateTest() throws JsonProcessingException {

//        FIND LAST ID
        Long id = book.getId();

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

        BookDTO bookToBeAltered = objectMapper.readValue(contentOfFindById, BookDTO.class);

//        SET USER
        bookToBeAltered.setAuthor("David Brubeck");
        bookToBeAltered.setLaunchDate("1965-11-29 13:50:05.878000");
        bookToBeAltered.setPrice(33.1);
        bookToBeAltered.setTitle("Time Out");

        var contentOfUpdate = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(bookToBeAltered)
                .when()
                .put()
                .then()
                .	statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
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
    @Order(4)
    void deleteTest() throws JsonProcessingException {
        Long id = book.getId();

        given(specification)
                .pathParam("id", id)
                .when()
                .delete("{id}")
                .then()
                .	statusCode(204);
    }

    @Test
    @Order(5)
    void findAllTest() throws JsonProcessingException {

        var content = given(specification)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParams("page", 0, "size", 12, "direction", "asc")
                .when()
                .get()
                .then()
                .	statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .body()
                .asString();

        WrapperBookDTO wrapper = objectMapper.readValue(content, WrapperBookDTO.class);
        List<BookDTO> books = wrapper.getEmbedded().getBooks();

        BookDTO bookOne = books.get(0);

        assertNotNull(bookOne.getId());
        assertTrue(bookOne.getId() > 0);

        assertEquals("Craig Larman", bookOne.getAuthor());
        assertEquals("2015-11-15 00:00:00", bookOne.getLaunchDate());
        assertEquals(144.98, bookOne.getPrice());
        assertEquals("Agile and Iterative Development: A Manager’s Guide", bookOne.getTitle());

        BookDTO bookFour = books.get(3);

        assertNotNull(bookFour.getId());
        assertTrue(bookFour.getId() > 0);

        assertEquals("Craig Larman", bookFour.getAuthor());
        assertEquals("1998-12-05 00:00:00", bookFour.getLaunchDate());
        assertEquals(43.82, bookFour.getPrice());
        assertEquals("Agile and Iterative Development: A Manager’s Guide", bookFour.getTitle());
    }
}
