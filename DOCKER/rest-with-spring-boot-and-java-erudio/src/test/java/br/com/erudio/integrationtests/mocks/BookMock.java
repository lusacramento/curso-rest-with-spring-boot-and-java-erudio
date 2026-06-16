package br.com.erudio.integrationtests.mocks;

import br.com.erudio.integrationtests.dto.BookDTO;

public class BookMock {
    public static BookDTO mockBook() {
        BookDTO mockedBook = new BookDTO();

        mockedBook.setAuthor("George S Clason");
        mockedBook.setLaunchDate("2017-11-29 13:50:05.878000");
        mockedBook.setPrice(69.26);
        mockedBook.setTitle("O homem mais rico da Babilônia");

        return mockedBook;
    }
}
