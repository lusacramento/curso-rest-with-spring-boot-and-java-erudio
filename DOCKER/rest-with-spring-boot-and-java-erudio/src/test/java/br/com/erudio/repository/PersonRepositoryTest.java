package br.com.erudio.repository;

import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.model.Person;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    PersonRepository repository;

    private static Person person;

    @BeforeAll
    static void setUp() {
        person = new Person();
    }

    @Order(1)
    @Test
    void findByName() {
        Pageable pageable = PageRequest.of(
                0,
                12,
                Sort.by(Sort.Direction.ASC, "firstName")
        );

        person = repository.findByName("iko", pageable).getContent().getFirst();

        assertNotNull(person);
        assertNotNull(person.getId());
        assertEquals("Nikola", person.getFirstName());
        assertEquals("Tesla", person.getLastName());
        assertEquals("Smiljan - Croatia", person.getAddress());
        assertEquals("Male", person.getGender());
        assertTrue(person.getEnabled());
        assertEquals("https://pub.erudio.com.br/meus-cursos", person.getProfileUrl());
        assertEquals("https://raw.githubusercontent.com/leandrocgsi/rest-with-spring-boot-and-java-erudio/refs/heads/main/photos/00_some_person.jpg", person.getPhotoUrl());
    }

    @Order(2)
    @Test
    @Transactional
    @Rollback(false)
    void disablePerson() {
        Long id = person.getId();

        repository. disablePerson(id);

        var result = repository.findById(id);

        assertNotNull(result.get().getId());
        assertNotNull(person.getId());

        assertEquals(person.getId(), result.get().getId());
        assertEquals(person.getFirstName(), result.get().getFirstName());
        assertEquals(person.getLastName(), result.get().getLastName());
        assertEquals(person.getAddress(), result.get().getAddress());
        assertEquals(person.getGender(), result.get().getGender());
        assertFalse(result.get().getEnabled());
        assertEquals(person.getProfileUrl(), result.get().getProfileUrl());
        assertEquals(person.getPhotoUrl(), result.get().getPhotoUrl());
    }
}