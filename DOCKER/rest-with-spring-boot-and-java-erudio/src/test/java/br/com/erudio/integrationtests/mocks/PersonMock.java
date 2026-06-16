package br.com.erudio.integrationtests.mocks;

import br.com.erudio.integrationtests.dto.PersonDTOV1;

public class PersonMock {
    public static PersonDTOV1 mockPerson(String gender) {
        PersonDTOV1 mockedPerson = new PersonDTOV1();
        switch (gender) {
            case "Male":
                mockedPerson.setFirstName("Richard");
                mockedPerson.setLastName("Stallman");
                mockedPerson.setAddress("New York City - New York - USA");
                mockedPerson.setGender("male");
                mockedPerson.setEnabled(true);
                mockedPerson.setPhotoUrl("https://wikipidia.com/richard_stallman");
                mockedPerson.setProfileUrl("https://pub.erudio.com.br/photo/rischard_stallman");
                break;
            case "Female":
                mockedPerson.setFirstName("Jenifer");
                mockedPerson.setLastName("Lorrane");
                mockedPerson.setAddress("Paris - France");
                mockedPerson.setGender("female");
                mockedPerson.setEnabled(true);
                mockedPerson.setPhotoUrl("https://wikipidia.com/jenifer_lorrane");
                mockedPerson.setProfileUrl("https://wikipidia.com/photo/jenifer_lorrane");
                break;
            case null, default:
                throw new RuntimeException("Gender is null or not informed!");
        }
        return mockedPerson;
    }
}
