package br.com.erudio.services;

import br.com.erudio.data.dto.BookDTO;
import br.com.erudio.exception.RequiredObjectIsNullException;
import br.com.erudio.model.Book;
import br.com.erudio.repository.BookRepository;
import br.com.erudio.utils.mocks.MockBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    MockBook input;

    @InjectMocks
    private BookService service;

    @Mock
    BookRepository repository;

    @BeforeEach
    void setUp(){
        input = new MockBook();
    }

    @Test
    void findById() {
        Book beforePersistingBook = input.mockEntity(1);
        beforePersistingBook.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(beforePersistingBook));

        var persistedBook =  service.findById(1L);
        assertNotNull(persistedBook);
        assertNotNull(persistedBook.getId());
        assertNotNull(persistedBook.getLinks());

        var links = persistedBook.getLinks();

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/book/1")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/book?page=1&size=12&direction=asc")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/book")
                        && "POST".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/book")
                        && "PUT".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/book/1")
                        && "DELETE".equals(link.getType())
                )
        );

        assertEquals(beforePersistingBook.getAuthor(), persistedBook.getAuthor());
        assertEquals(beforePersistingBook.getLaunchDate(), persistedBook.getLaunchDate());
        assertEquals(beforePersistingBook.getPrice(), persistedBook.getPrice());
        assertEquals(beforePersistingBook.getTitle(), persistedBook.getTitle());
    }

    @Test
    void testCreateWithNullBook(){
        Exception exception = assertThrows(RequiredObjectIsNullException.class,
                () -> {
                    service.create(null);
                });

        String expectedMessage = "It is not allowed to persist a null object";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void create() {
        Book beforePersistingBook = input.mockEntity(1);;
        beforePersistingBook.setId(1L);
        BookDTO dto = input.mockDTO(1);


        when(repository.save(any(Book.class))).thenReturn(beforePersistingBook);

        var persistedBook = service.create(dto);
        assertNotNull(persistedBook);
        assertNotNull(persistedBook.getId());
        assertNotNull(persistedBook.getLinks());

        var links = persistedBook.getLinks();

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/book/1")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/book?page=1&size=12&direction=asc")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/book")
                        && "POST".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/book")
                        && "PUT".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/book/1")
                        && "DELETE".equals(link.getType())
                )
        );

        assertEquals(beforePersistingBook.getAuthor(), persistedBook.getAuthor());
        assertEquals(beforePersistingBook.getLaunchDate(), persistedBook.getLaunchDate());
        assertEquals(beforePersistingBook.getPrice(), persistedBook.getPrice());
        assertEquals(beforePersistingBook.getTitle(), persistedBook.getTitle());
    }

    @Test
    void testUpdateWithNullBook(){
        Exception exception = assertThrows(RequiredObjectIsNullException.class,
                () -> {
                    service.update(null);
                });

        String expectedMessage = "It is not allowed to persist a null object";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void update() {
        Book beforePersistingBook = input.mockEntity(1);;
        beforePersistingBook.setId(1L);
        BookDTO dto = input.mockDTO(1);
        when(repository.findById(1L)).thenReturn(Optional.of(beforePersistingBook));
        when(repository.save(any(Book.class))).thenReturn(beforePersistingBook);

        var persistedBook = service.update(dto);
        assertNotNull(persistedBook);
        assertNotNull(persistedBook.getId());
        assertNotNull(persistedBook.getLinks());

        var links = persistedBook.getLinks();
        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/book/1")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/book?page=1&size=12&direction=asc")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/book")
                        && "POST".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/book")
                        && "PUT".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/book/1")
                        && "DELETE".equals(link.getType())
                )
        );

        assertEquals(beforePersistingBook.getAuthor(), persistedBook.getAuthor());
        assertEquals(beforePersistingBook.getLaunchDate(), persistedBook.getLaunchDate());
        assertEquals(beforePersistingBook.getPrice(), persistedBook.getPrice());
        assertEquals(beforePersistingBook.getTitle(), persistedBook.getTitle());
    }

    @Test
    void delete() {
        Book book = input.mockEntity(1);
        book.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(book));

        service.delete(1L);
        verify(repository, times(1)).findById(anyLong());
        verify(repository, times(1)).delete(any(Book.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    @Disabled("REASON: Still under development")
    void findAll() {
        List<Book> list = input.mockEntityList();
        when(repository.findAll()).thenReturn(list);
        List<BookDTO> books =  new ArrayList<>();// service.findAll(pageable);

        assertNotNull(books);
        assertEquals(14, books.size());

        var personOne = books.get(1);

        assertNotNull(personOne);
        assertNotNull(personOne.getId());
        assertNotNull(personOne.getLinks());

        var links1 = personOne.getLinks();

        assertTrue(links1.stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/book/1")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links1.stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/book")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links1.stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/book")
                        && "POST".equals(link.getType())
                )
        );

        assertTrue(links1.stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/book")
                        && "PUT".equals(link.getType())
                )
        );

        assertTrue(links1.stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/book/1")
                        && "DELETE".equals(link.getType())
                )
        );

        assertEquals("Franz Fanon - 1", personOne.getAuthor());
        assertEquals("2020-11-29 13:50:05.878000", personOne.getLaunchDate());
        assertEquals(65.10, personOne.getPrice());
        assertEquals("Peau noire, masques blancs - 1ª edicao", personOne.getTitle());

        var personFour = books.get(4);

        assertNotNull(personFour);
        assertNotNull(personFour.getId());
        assertNotNull(personFour.getLinks());

        var links4 = personFour.getLinks();
        assertTrue(links4.stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/book/4")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links4.stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/book")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links4.stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/book")
                        && "POST".equals(link.getType())
                )
        );

        assertTrue(links4.stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/book")
                        && "PUT".equals(link.getType())
                )
        );

        assertTrue(links4.stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/book/4")
                        && "DELETE".equals(link.getType())
                )
        );

        assertEquals("Franz Fanon - 1", personFour.getAuthor());
        assertEquals("2020-11-29 13:50:05.878000", personFour.getLaunchDate());
        assertEquals(65.10, personFour.getPrice());
        assertEquals("Peau noire, masques blancs - 4ª edição", personFour.getTitle());

        var personSeven = books.get(7);

        assertNotNull(personSeven);
        assertNotNull(personSeven.getId());
        assertNotNull(personSeven.getLinks());

        var links7 = personSeven.getLinks();

        assertTrue(links7.stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/book/7")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links7.stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/book")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links7.stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/book")
                        && "POST".equals(link.getType())
                )
        );

        assertTrue(links7.stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/book")
                        && "PUT".equals(link.getType())
                )
        );

        assertTrue(links7.stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/book/7")
                        && "DELETE".equals(link.getType())
                )
        );

        assertEquals("Franz Fanon - 7", personSeven.getAuthor());
        assertEquals("2020-11-29 13:50:05.878000", personSeven.getLaunchDate());
        assertEquals(65.10, personSeven.getPrice());
        assertEquals("Peau noire, masques blancs - 7ª edicao", personSeven.getTitle());
    }
}
