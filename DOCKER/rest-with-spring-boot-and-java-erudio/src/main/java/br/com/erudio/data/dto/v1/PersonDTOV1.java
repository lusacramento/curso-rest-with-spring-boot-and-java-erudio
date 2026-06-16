package br.com.erudio.data.dto.v1;

import br.com.erudio.model.Book;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Relation(collectionRelation = "people")
public class PersonDTOV1 extends RepresentationModel<PersonDTOV1> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

//    @JsonProperty("first_name")
    private String firstName;

//    @JsonProperty("last_name")
    private String lastName;

    private String address;

//    @JsonIgnore
    private String gender;

    private Boolean enabled;

    private String profileUrl;

    private String photoUrl;

    @JsonIgnore
    private List<Book> books;

    @JsonIgnore
    public String getName(){
        return (firstName != null ? firstName : "") +
                (lastName != null? " " + lastName : "");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PersonDTOV1 that = (PersonDTOV1) o;
        return Objects.equals(id, that.id) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(address, that.address) && Objects.equals(gender, that.gender) && Objects.equals(enabled, that.enabled) && Objects.equals(profileUrl, that.profileUrl) && Objects.equals(photoUrl, that.photoUrl) && Objects.equals(books, that.books);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, firstName, lastName, address, gender, enabled, profileUrl, photoUrl, books);
    }
}
