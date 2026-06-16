package br.com.erudio.utils.mapper;

import br.com.erudio.data.dto.v1.PersonDTOV1;
import br.com.erudio.model.Person;
import br.com.erudio.utils.mocks.MockPerson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static br.com.erudio.mapper.ObjectMapper.parseListObjects;
import static br.com.erudio.mapper.ObjectMapper.parseObject;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectMapperPersonTests {
    MockPerson inputObject;

    @BeforeEach
    public void setUp() {
        inputObject = new MockPerson();
    }

    @Test
    public void parseEntityToDTOTest() {
        PersonDTOV1 output = parseObject(inputObject.mockEntity(), PersonDTOV1.class);
        assertEquals(Long.valueOf(0L), output.getId());
        assertEquals("First Name Test0", output.getFirstName());
        assertEquals("Last Name Test0", output.getLastName());
        assertEquals("Address Test0", output.getAddress());
        assertEquals("Male", output.getGender());
        assertFalse(output.getEnabled());
        assertEquals("https://wikipedia.com/fullname0", output.getProfileUrl());
        assertEquals("https://wikipedia.com/photo_fullname0", output.getPhotoUrl());
    }

    @Test
    public void parseEntityListToDTOListTest() {
        List<PersonDTOV1> outputList = parseListObjects(inputObject.mockEntityList(), PersonDTOV1.class);
        PersonDTOV1 outputZero = outputList.get(0);

        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("First Name Test0", outputZero.getFirstName());
        assertEquals("Last Name Test0", outputZero.getLastName());
        assertEquals("Address Test0", outputZero.getAddress());
        assertEquals("Male", outputZero.getGender());
        assertFalse(outputZero.getEnabled());
        assertEquals("https://wikipedia.com/fullname0", outputZero.getProfileUrl());
        assertEquals("https://wikipedia.com/photo_fullname0", outputZero.getPhotoUrl());

        PersonDTOV1 outputSeven = outputList.get(7);

        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("First Name Test7", outputSeven.getFirstName());
        assertEquals("Last Name Test7", outputSeven.getLastName());
        assertEquals("Address Test7", outputSeven.getAddress());
        assertEquals("Female", outputSeven.getGender());
        assertFalse(outputSeven.getEnabled());
        assertEquals("https://wikipedia.com/fullname7", outputSeven.getProfileUrl());
        assertEquals("https://wikipedia.com/photo_fullname7", outputSeven.getPhotoUrl());

        PersonDTOV1 outputTwelve = outputList.get(12);

        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("First Name Test12", outputTwelve.getFirstName());
        assertEquals("Last Name Test12", outputTwelve.getLastName());
        assertEquals("Address Test12", outputTwelve.getAddress());
        assertEquals("Male", outputTwelve.getGender());
        assertFalse(outputTwelve.getEnabled());
        assertEquals("https://wikipedia.com/fullname12", outputTwelve.getProfileUrl());
        assertEquals("https://wikipedia.com/photo_fullname12", outputTwelve.getPhotoUrl());
    }

    @Test
    public void parseDTOToEntityTest() {
        Person output = parseObject(inputObject.mockDTO(), Person.class);
        assertEquals(Long.valueOf(0L), output.getId());
        assertEquals("First Name Test0", output.getFirstName());
        assertEquals("Last Name Test0", output.getLastName());
        assertEquals("Address Test0", output.getAddress());
        assertEquals("Male", output.getGender());
        assertTrue(output.getEnabled());
        assertEquals("https://wikipedia.com/fullname0", output.getProfileUrl());
        assertEquals("https://wikipedia.com/photo_fullname0", output.getPhotoUrl());
    }

    @Test
    public void parserDTOListToEntityListTest() {
        List<Person> outputList = parseListObjects(inputObject.mockDTOList(), Person.class);
        Person outputZero = outputList.get(0);

        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("First Name Test0", outputZero.getFirstName());
        assertEquals("Last Name Test0", outputZero.getLastName());
        assertEquals("Address Test0", outputZero.getAddress());
        assertEquals("Male", outputZero.getGender());
        assertTrue(outputZero.getEnabled());
        assertEquals("https://wikipedia.com/fullname0", outputZero.getProfileUrl());
        assertEquals("https://wikipedia.com/photo_fullname0", outputZero.getPhotoUrl());

        Person outputSeven = outputList.get(7);

        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("First Name Test7", outputSeven.getFirstName());
        assertEquals("Last Name Test7", outputSeven.getLastName());
        assertEquals("Address Test7", outputSeven.getAddress());
        assertEquals("Female", outputSeven.getGender());
        assertTrue(outputSeven.getEnabled());
        assertEquals("https://wikipedia.com/fullname7", outputSeven.getProfileUrl());
        assertEquals("https://wikipedia.com/photo_fullname7", outputSeven.getPhotoUrl());

        Person outputTwelve = outputList.get(12);

        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("First Name Test12", outputTwelve.getFirstName());
        assertEquals("Last Name Test12", outputTwelve.getLastName());
        assertEquals("Address Test12", outputTwelve.getAddress());
        assertEquals("Male", outputTwelve.getGender());
        assertTrue(outputTwelve.getEnabled());
        assertEquals("https://wikipedia.com/fullname12", outputTwelve.getProfileUrl());
        assertEquals("https://wikipedia.com/photo_fullname12", outputTwelve.getPhotoUrl());
    }
}