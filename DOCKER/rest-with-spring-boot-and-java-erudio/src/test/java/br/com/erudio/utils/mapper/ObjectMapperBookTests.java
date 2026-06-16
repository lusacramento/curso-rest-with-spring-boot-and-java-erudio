package br.com.erudio.utils.mapper;

import br.com.erudio.data.dto.BookDTO;
import br.com.erudio.model.Book;
import br.com.erudio.utils.mocks.MockBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static br.com.erudio.mapper.ObjectMapper.parseListObjects;
import static br.com.erudio.mapper.ObjectMapper.parseObject;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectMapperBookTests {
    MockBook inputObject;

    @BeforeEach
    public void setUp() {
        inputObject = new MockBook();
    }

    @Test
    public void parseEntityToDTOTest() {
        BookDTO output = parseObject(inputObject.mockEntity(), BookDTO.class);
        assertEquals(Long.valueOf(0L), output.getId());
        assertEquals("Franz Fanon - 0", output.getAuthor());
        assertEquals("2020-11-29 13:50:05.878000", output.getLaunchDate());
        assertEquals(65.10, output.getPrice());
        assertEquals("Peau noire, masques blancs - 0 edition", output.getTitle());
    }

    @Test
    public void parseEntityListToDTOListTest() {
        List<BookDTO> outputList = parseListObjects(inputObject.mockEntityList(), BookDTO.class);
        BookDTO outputZero = outputList.get(0);

        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("Franz Fanon - 0", outputZero.getAuthor());
        assertEquals("2020-11-29 13:50:05.878000", outputZero.getLaunchDate());
        assertEquals(65.10, outputZero.getPrice());
        assertEquals("Peau noire, masques blancs - 0 edition", outputZero.getTitle());

        BookDTO outputSeven = outputList.get(7);

        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("Franz Fanon - 7", outputSeven.getAuthor());
        assertEquals("2020-11-29 13:50:05.878000", outputSeven.getLaunchDate());
        assertEquals(65.10, outputSeven.getPrice());
        assertEquals("Peau noire, masques blancs - 7 edition", outputSeven.getTitle());

        BookDTO outputTwelve = outputList.get(12);

        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("Franz Fanon - 12", outputTwelve.getAuthor());
        assertEquals("2020-11-29 13:50:05.878000", outputTwelve.getLaunchDate());
        assertEquals(65.10, outputTwelve.getPrice());
        assertEquals("Peau noire, masques blancs - 12 edition", outputTwelve.getTitle());
    }

    @Test
    public void parseDTOToEntityTest() {
        Book output = parseObject(inputObject.mockDTO(), Book.class);
        assertEquals(Long.valueOf(0L), output.getId());
        assertEquals("Franz Fanon - 0", output.getAuthor());
        assertEquals("2020-11-29 13:50:05.878000", output.getLaunchDate());
        assertEquals(65.10, output.getPrice());
        assertEquals("Peau noire, masques blancs - 0 edition", output.getTitle());
    }

    @Test
    public void parserDTOListToEntityListTest() {
        List<Book> outputList = parseListObjects(inputObject.mockDTOList(), Book.class);
        Book outputZero = outputList.get(0);

        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("Franz Fanon - 0", outputZero.getAuthor());
        assertEquals("2020-11-29 13:50:05.878000", outputZero.getLaunchDate());
        assertEquals(65.10, outputZero.getPrice());
        assertEquals("Peau noire, masques blancs - 0 edition", outputZero.getTitle());

        Book outputSeven = outputList.get(7);

        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("Franz Fanon - 7", outputSeven.getAuthor());
        assertEquals("2020-11-29 13:50:05.878000", outputSeven.getLaunchDate());
        assertEquals(65.1, outputSeven.getPrice());
        assertEquals("Peau noire, masques blancs - 7 edition", outputSeven.getTitle());

        Book outputTwelve = outputList.get(12);

        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("Franz Fanon - 12", outputTwelve.getAuthor());
        assertEquals("2020-11-29 13:50:05.878000", outputTwelve.getLaunchDate());
        assertEquals(65.1, outputTwelve.getPrice());
        assertEquals("Peau noire, masques blancs - 12 edition", outputTwelve.getTitle());
    }
}