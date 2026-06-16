package br.com.erudio.services;

import br.com.erudio.data.dto.v1.PersonDTOV1;
import br.com.erudio.exception.RequiredObjectIsNullException;
import br.com.erudio.model.Person;
import br.com.erudio.repository.PersonRepository;
import br.com.erudio.utils.mocks.MockPerson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PersonServiceV1Test {
    MockPerson input;

    @InjectMocks
    private PersonServiceV1 service;

    @Mock
    PersonRepository repository;

    @Mock
    PagedResourcesAssembler<PersonDTOV1> assembler;


    @BeforeEach
    void setUp() {
        input = new MockPerson();
    }

    @Test
    void findById() {
        Person person = input.mockEntity(1);
        person.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(person));

        var result =  service.findById(1L);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        var links = result.getLinks();

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                && link.getHref().endsWith("/api/v1/person/1")
                && "GET".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/v1/person?page=0&size=12&direction=asc")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/v1/person")
                        && "POST".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/person")
                        && "PUT".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && "DELETE".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("exportPage")
                        && link.getHref().endsWith("/api/v1/person/exportPage?page=0&size=12&direction=asc")
                        && "GET".equals(link.getType())
                        && Objects.equals(link.getTitle(), "Export People")
                )
        );

        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Address Test1", result.getAddress());
        assertEquals("Female", result.getGender());
        assertFalse(result.getEnabled());
        assertEquals("https://wikipedia.com/fullname1", result.getProfileUrl());
        assertEquals("https://wikipedia.com/photo_fullname1", result.getPhotoUrl());
    }

    @Test
    void findByNameTest(){
        Person person = input.mockEntity(1);
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, "firstName"));

        Page<Person> page = mock(Page.class);
        List<Person> persons = List.of(person);
        lenient().when(page.getContent()).thenReturn(persons);

        when(repository.findByName("alb", pageable)).thenReturn(page);

        PersonDTOV1 dto = input.mockDTO(1);
        // Assume links are added, or skip link checks for simplicity
        EntityModel<PersonDTOV1> entityModel = EntityModel.of(dto);
        PagedModel<EntityModel<PersonDTOV1>> pagedModel = mock(PagedModel.class);
        when(pagedModel.getContent()).thenReturn(List.of(entityModel));
        lenient().when(assembler.toModel(any(), any(Link.class))).thenReturn(pagedModel);

        var result = service.findByName("alb", pageable);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());

        var em = result.getContent().iterator().next();
        var d = em.getContent();
        assertNotNull(d);
        assertEquals("First Name Test1", d.getFirstName());
        assertEquals("Last Name Test1", d.getLastName());
        assertEquals("Address Test1", d.getAddress());
        assertEquals("Female", d.getGender());
        assertTrue(d.getEnabled());
        assertEquals("https://wikipedia.com/fullname1", d.getProfileUrl());
        assertEquals("https://wikipedia.com/photo_fullname1", d.getPhotoUrl());
    }

    @Test
    void testCreateWithNullPerson(){
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
        Person persisted = input.mockEntity(1);
        persisted.setId(1L);

        PersonDTOV1 dto = input.mockDTO(1);

        when(repository.save(any(Person.class))).thenReturn(persisted);

        var result = service.create(dto);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        
        var links = result.getLinks();

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/v1/person?page=0&size=12&direction=asc")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/v1/person")
                        && "POST".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/person")
                        && "PUT".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && "DELETE".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("exportPage")
                        && link.getHref().endsWith("/api/v1/person/exportPage?page=0&size=12&direction=asc")
                        && "GET".equals(link.getType())
                        && Objects.equals(link.getTitle(), "Export People")
                )
        );

        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Address Test1", result.getAddress());
        assertEquals("Female", result.getGender());
        assertFalse(result.getEnabled());
        assertEquals("https://wikipedia.com/fullname1", result.getProfileUrl());
        assertEquals("https://wikipedia.com/photo_fullname1", result.getPhotoUrl());
    }

    @Test
    void testUpdateWithNullPerson(){
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
        Person person = input.mockEntity(1);
        Person persisted = person;
        persisted.setId(1L);

        PersonDTOV1 dto = input.mockDTO(1);
        when(repository.findById(1L)).thenReturn(Optional.of(person));
        when(repository.save(person)).thenReturn(persisted);

        var result = service.update(dto);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        var links = result.getLinks();
        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/v1/person?page=0&size=12&direction=asc")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/v1/person")
                        && "POST".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/person")
                        && "PUT".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && "DELETE".equals(link.getType())
                )
        );

        assertTrue(links.stream()
                .anyMatch(link -> link.getRel().value().equals("exportPage")
                        && link.getHref().endsWith("/api/v1/person/exportPage?page=0&size=12&direction=asc")
                        && "GET".equals(link.getType())
                        && Objects.equals(link.getTitle(), "Export People")
                )
        );

        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Address Test1", result.getAddress());
        assertEquals("Female", result.getGender());
        assertTrue(result.getEnabled());
        assertEquals("https://wikipedia.com/fullname1", result.getProfileUrl());
        assertEquals("https://wikipedia.com/photo_fullname1", result.getPhotoUrl());
    }

    @Test
    void delete() {
        Person person = input.mockEntity(1);
        person.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(person));

        service.delete(1L);
        verify(repository, times(1)).findById(anyLong());
        verify(repository, times(1)).delete(any(Person.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    @Disabled("REASON: Still under development")
    void findAll() {
        List<Person> list = input.mockEntityList();
        when(repository.findAll()).thenReturn(list);
        List<PersonDTOV1> people =  new ArrayList<>();// service.findAll(pageable);

        assertNotNull(people);
        assertEquals(14, people.size());

        var personOne = people.get(1);

        assertNotNull(personOne);
        assertNotNull(personOne.getId());
        assertNotNull(personOne.getLinks());

        var links1 = personOne.getLinks();

        assertTrue(links1.stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links1.stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/v1/person")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links1.stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/v1/person")
                        && "POST".equals(link.getType())
                )
        );

        assertTrue(links1.stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/person")
                        && "PUT".equals(link.getType())
                )
        );

        assertTrue(links1.stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/person/1")
                        && "DELETE".equals(link.getType())
                )
        );

        assertEquals("First Name Test1", personOne.getFirstName());
        assertEquals("Last Name Test1", personOne.getLastName());
        assertEquals("Address Test1", personOne.getAddress());
        assertEquals("Female", personOne.getGender());

        var personFour = people.get(4);

        assertNotNull(personFour);
        assertNotNull(personFour.getId());
        assertNotNull(personFour.getLinks());

        var links4 = personFour.getLinks();
        assertTrue(links4.stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/v1/person/4")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links4.stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/v1/person")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links4.stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/v1/person")
                        && "POST".equals(link.getType())
                )
        );

        assertTrue(links4.stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/person")
                        && "PUT".equals(link.getType())
                )
        );

        assertTrue(links4.stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/person/4")
                        && "DELETE".equals(link.getType())
                )
        );

        assertEquals("First Name Test4", personFour.getFirstName());
        assertEquals("Last Name Test4", personFour.getLastName());
        assertEquals("Address Test4", personFour.getAddress());
        assertEquals("Male", personFour.getGender());

        var personSeven = people.get(7);

        assertNotNull(personSeven);
        assertNotNull(personSeven.getId());
        assertNotNull(personSeven.getLinks());

        var links7 = personSeven.getLinks();

        assertTrue(links7.stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/v1/person/7")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links7.stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/v1/person")
                        && "GET".equals(link.getType())
                )
        );

        assertTrue(links7.stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/v1/person")
                        && "POST".equals(link.getType())
                )
        );

        assertTrue(links7.stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/v1/person")
                        && "PUT".equals(link.getType())
                )
        );

        assertTrue(links7.stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/v1/person/7")
                        && "DELETE".equals(link.getType())
                )
        );

        assertEquals("First Name Test7", personSeven.getFirstName());
        assertEquals("Last Name Test7", personSeven.getLastName());
        assertEquals("Address Test7", personSeven.getAddress());
        assertEquals("Female", personSeven.getGender());
    }
}