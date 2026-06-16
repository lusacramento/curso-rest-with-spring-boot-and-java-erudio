package br.com.erudio.file.exporter.impl;

import br.com.erudio.data.dto.v1.PersonDTOV1;
import br.com.erudio.file.exporter.contract.PersonExporter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvExporter implements PersonExporter {
    @Override
    public Resource exportPeople(List<PersonDTOV1> people) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        CSVFormat csvFormat = CSVFormat.Builder.create()
                .setHeader("id", "firstName", "lastName", "address", "gender", "enabled")
                .setSkipHeaderRecord(false).build();

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)){
            for(PersonDTOV1 person : people){
                csvPrinter.printRecord(
                        person.getId(),
                        person.getFirstName(),
                        person.getLastName(),
                        person.getAddress(),
                        person.getGender(),
                        person.getEnabled()
                );
            }
        }
        catch (Exception e) {
    throw new RuntimeException(e);
}

        return new ByteArrayResource(outputStream.toByteArray());
    }

    @Override
    public Resource exportPerson(PersonDTOV1 person) throws Exception {
        return null;
    }
}
