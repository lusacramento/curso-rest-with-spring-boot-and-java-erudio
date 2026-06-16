package br.com.erudio.utils.mocks;

import br.com.erudio.data.dto.v1.PersonDTOV1;
import br.com.erudio.model.Person;

import java.util.ArrayList;
import java.util.List;

public class MockPerson {

    public Person mockEntity() {
        return mockEntity(0);
    }

    public PersonDTOV1 mockDTO() {
        return mockDTO(0);
    }
    
    public List<Person> mockEntityList() {
        List<Person> persons = new ArrayList<Person>();
        for (int i = 0; i < 14; i++) {
            persons.add(mockEntity(i));
        }
        return persons;
    }

    public List<PersonDTOV1> mockDTOList() {
        List<PersonDTOV1> persons = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            persons.add(mockDTO(i));
        }
        return persons;
    }
    
    public Person mockEntity(Integer number) {
        Person person = new Person();
        person.setId(number.longValue());
        person.setAddress("Address Test" + number);
        person.setFirstName("First Name Test" + number);
        person.setGender(((number % 2)==0) ? "Male" : "Female");
        person.setEnabled(false);
        person.setLastName("Last Name Test" + number);
        person.setProfileUrl("https://wikipedia.com/fullname" + number);
        person.setPhotoUrl("https://wikipedia.com/photo_fullname" + number);
        return person;
    }

    public PersonDTOV1 mockDTO(Integer number) {
        PersonDTOV1 person = new PersonDTOV1();
        person.setId(number.longValue());
        person.setAddress("Address Test" + number);
        person.setGender(((number % 2)==0) ? "Male" : "Female");
        person.setEnabled(true);
        person.setFirstName("First Name Test" + number);
        person.setLastName("Last Name Test" + number);
        person.setProfileUrl("https://wikipedia.com/fullname" + number);
        person.setPhotoUrl("https://wikipedia.com/photo_fullname" + number);
        return person;
    }
}