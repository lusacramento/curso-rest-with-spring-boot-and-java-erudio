package br.com.erudio.integrationtests.controllers.withxml;

import br.com.erudio.config.TestConfigs;
import br.com.erudio.integrationtests.dto.BookDTO;
import br.com.erudio.integrationtests.dto.security.TokenDTO;
import br.com.erudio.integrationtests.dto.wrappers.xml.PagedModelBook;
import br.com.erudio.integrationtests.mocks.BookMock;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.utils.auth.Authenticator;
import br.com.erudio.utils.specification.Specification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerXmlTest extends AbstractIntegrationTest {
    private static TokenDTO tokenDto;

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static BookDTO book;

    @BeforeAll
    static void setUp() {
        tokenDto = new TokenDTO();

        objectMapper = new XmlMapper();
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
        ).build();;
        var content = given(specification)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .body(book)
                .when()
                    .post()
                .then()
                .	statusCode(201)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()
                    .body()
                    .asString();

        BookDTO createBook = objectMapper.readValue(content, BookDTO.class);
        book = createBook;

        assertNotNull(createBook.getId());
        assertTrue(createBook.getId() > 0);

        assertEquals(book.getAuthor(), createBook.getAuthor());
        assertEquals(book.getLaunchDate(), createBook.getLaunchDate());
        assertEquals(book.getPrice(), createBook.getPrice());
        assertEquals(book.getTitle(), createBook.getTitle());
    }

    @Test
    @Order(2)
    void findByIdTest() throws JsonProcessingException {

        var content = given(specification)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .pathParam("id", book.getId())
                .when()
                .get("{id}")
                .then()
                .	statusCode(200)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()
                .body()
                .asString();

        BookDTO createBook = objectMapper.readValue(content, BookDTO.class);
        book = createBook;

        assertNotNull(createBook.getId());
        assertTrue(createBook.getId() > 0);

        assertEquals(book.getAuthor(), createBook.getAuthor());
        assertEquals(book.getLaunchDate(), createBook.getLaunchDate());
        assertEquals(book.getPrice(), createBook.getPrice());
        assertEquals(book.getTitle(), createBook.getTitle());
    }

    @Test
    @Order(3)
    void updateTest() throws JsonProcessingException {
        
//        FIND LAST ID
        Long id = book.getId();

        var contentOfFindById = given(specification)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .pathParam("id", id)
                .when()
                .get("{id}")
                .then()
                .	statusCode(200)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()
                .body()
                .asString();

        BookDTO bookToBeAltered = objectMapper.readValue(contentOfFindById, BookDTO.class);

//        SET USER
        bookToBeAltered.setAuthor(" Cullen Bunn ");
        bookToBeAltered.setLaunchDate("2023-11-29 13:50:05.878000");
        bookToBeAltered.setPrice(19.9);
        bookToBeAltered.setTitle("Cyberpunk 2077: trauma team");

        var contentOfUpdate = given(specification)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .body(bookToBeAltered)
                .when()
                .put()
                .then()
                .	statusCode(200)
                .contentType(MediaType.APPLICATION_XML_VALUE)
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
                .accept(MediaType.APPLICATION_XML_VALUE)
                .queryParams("page", 0, "size", 5, "direction", "asc")
                .when()
                .get()
                .then()
                .	statusCode(200)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()
                .body()
                .asString();

        PagedModelBook wrapper = objectMapper.readValue(content, PagedModelBook.class);

        List<BookDTO> book = wrapper.getContent();

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
        assertEquals("2015-11-15 00:00:00", bookFour.getLaunchDate());
        assertEquals(144.98, bookFour.getPrice());
        assertEquals("Agile and Iterative Development: A Manager’s Guide", bookFour.getTitle());
    }
}
