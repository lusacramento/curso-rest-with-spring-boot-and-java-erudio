package br.com.erudio.utils.mocks;

import br.com.erudio.data.dto.BookDTO;
import br.com.erudio.model.Book;

import java.util.ArrayList;
import java.util.List;

public class MockBook {

    public Book mockEntity() {
        return mockEntity(0);
    }

    public BookDTO mockDTO() {
        return mockDTO(0);
    }

    public List<Book> mockEntityList(){
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < 14; i++){
            books.add(mockEntity(i));
        }
        return books;
    }

    public List<Book> mockDTOList(){
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < 14; i++){
            books.add(mockEntity(i));
        }
        return books;
    }

    public Book mockEntity(Integer number){
        Book book = new Book();
        book.setId(number.longValue());
        book.setAuthor("Franz Fanon - " + number);
        book.setLaunchDate("2020-11-29 13:50:05.878000");
        book.setPrice(65.10);
        book.setTitle("Peau noire, masques blancs - " + number + " edition");
        return book;
    }

    public BookDTO mockDTO (Integer number){
        BookDTO book = new BookDTO();
        book.setId(number.longValue());
        book.setAuthor("Franz Fanon - " + number);
        book.setLaunchDate("2020-11-29 13:50:05.878000");
        book.setPrice(65.10 + number);
        book.setTitle("Peau noire, masques blancs - " + number + " edition");
        return book;
    }
}
