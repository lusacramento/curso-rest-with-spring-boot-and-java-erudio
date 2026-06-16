package br.com.erudio.data.dto;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Relation(collectionRelation = "books")
public class BookDTO extends RepresentationModel<BookDTO> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String author;

    private String launchDate;

    private Double price;

    private String title;

    public BookDTO(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(String launchDate) {
        this.launchDate = launchDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BookDTO bookDTO = (BookDTO) o;
        return Objects.equals(getId(), bookDTO.getId()) && Objects.equals(getAuthor(), bookDTO.getAuthor()) && Objects.equals(getLaunchDate(), bookDTO.getLaunchDate()) && Objects.equals(getPrice(), bookDTO.getPrice()) && Objects.equals(getTitle(), bookDTO.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAuthor(), getLaunchDate(), getPrice(), getTitle());
    }
}
