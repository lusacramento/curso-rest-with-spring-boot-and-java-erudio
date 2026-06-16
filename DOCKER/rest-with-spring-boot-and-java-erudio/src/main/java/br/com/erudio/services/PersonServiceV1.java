package br.com.erudio.services;

import br.com.erudio.controllers.PersonControllerV1;
import br.com.erudio.data.dto.v1.PersonDTOV1;
import br.com.erudio.exception.RequiredObjectIsNullException;
import br.com.erudio.exception.ResourceNotFoundException;
import br.com.erudio.exception.handler.BadRequestException;
import br.com.erudio.exception.handler.FileStorageException;
import br.com.erudio.file.exporter.contract.PersonExporter;
import br.com.erudio.file.exporter.factory.FileExporterFactory;
import br.com.erudio.file.importer.contract.FileImporter;
import br.com.erudio.file.importer.factory.FileImporterFactory;
import br.com.erudio.model.Person;
import br.com.erudio.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static br.com.erudio.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonServiceV1 {

    private final Logger logger = Logger.getLogger(PersonServiceV1.class.getName());

    @Autowired
    PersonRepository repository;

    @Autowired
    PagedResourcesAssembler<PersonDTOV1> assembler;

    @Autowired
    FileImporterFactory importer;

    @Autowired
    FileExporterFactory exporter;

    public PagedModel<EntityModel<PersonDTOV1>> findAll(Pageable pageable){
        logger.info("Finding people...");

        var people = repository.findAll(pageable);

        return buildPagedModel(pageable, people);
    }

    public PagedModel<EntityModel<PersonDTOV1>> findByName(String firstName, Pageable pageable){
        logger.info("Finding people...");

        var people = repository.findByName(firstName, pageable);

        return buildPagedModel(pageable, people);
    }

    public Resource exportPerson(Long id, String acceptHeader){
        logger.info("Exporting date of one Person");

        var person = repository.findById(id)
                .map(entity -> parseObject(entity, PersonDTOV1.class))
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this Id"));

        try {
            PersonExporter exporter = this.exporter.getExporter(acceptHeader);
            return exporter.exportPerson(person);
        } catch (Exception e) {
            throw new RuntimeException("Error during file export!", e);

        }
    }

    public PersonDTOV1 findById(Long id){
        logger.info("Finding one Person...");

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this Id"));
        var dto = parseObject(entity, PersonDTOV1.class);
        addHateoasLinks(dto);
        return dto;
    }

    public PersonDTOV1 create(PersonDTOV1 person) {
        if(person == null) throw new RequiredObjectIsNullException();

        logger.info("Creating person for \\person.getFirstName()...");
        var entity = parseObject(person, Person.class);

        var dto = parseObject(repository.save(entity), PersonDTOV1.class);
        addHateoasLinks(dto);
        return dto;
    }

    public PersonDTOV1 update(PersonDTOV1 person){
        if(person == null) throw new RequiredObjectIsNullException();

        logger.info("Updating person...");

        Person entity = repository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this Id"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());
        entity.setEnabled(person.getEnabled());
        entity.setProfileUrl(person.getProfileUrl());
        entity.setPhotoUrl(person.getPhotoUrl());

        var dto = parseObject(repository.save(entity), PersonDTOV1.class);
        addHateoasLinks(dto);
        return dto;
    }

    @Transactional
    public PersonDTOV1 disablePerson(Long id){
        logger.info("Disabling one Person...");

        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this Id"));

        repository.disablePerson(id);

        var entity = repository.findById(id).get();
        var dto = parseObject(entity, PersonDTOV1.class);
        addHateoasLinks(dto);

        return dto;
    }
    public void delete(Long id){
        logger.info("Deleting one Person...");

        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this Id"));

        repository.delete(entity);
    }

    public List<PersonDTOV1> massCreation(MultipartFile file){
        logger.info("Importing people from file...");

        if(file.isEmpty()) throw new BadRequestException("Please set a valid file!");

        try(InputStream inputStream = file.getInputStream()){
            String fileName = Optional.ofNullable(file.getOriginalFilename())
                    .orElseThrow(() -> new BadRequestException("File name cannot be null!"));

            FileImporter importer = this.importer.getImporter(fileName);
            List<Person> entities = importer.importFile(inputStream)
                    .stream().map(dto -> repository.save(parseObject(dto, Person.class)))
                    .toList();

            return entities.stream()
                    .map(entity -> {
                        var dto = parseObject(entity, PersonDTOV1.class);
                        addHateoasLinks(dto);
                        return dto;
                    }).toList();

        } catch (Exception e) {
            throw new FileStorageException("Error processing the file!", e);
        }
    }

    public Resource exportPage(Pageable pageable, String acceptHeader){
        logger.info("Exporting a people page...");

        var people = repository.findAll(pageable)
                .map(person -> parseObject(person, PersonDTOV1.class))
                .getContent();

        try {
            PersonExporter exporter = this.exporter.getExporter(acceptHeader);
            return exporter.exportPeople(people);
        } catch (Exception e) {
            throw new RuntimeException("Error during file export!", e);
        }
    }

    private PagedModel<EntityModel<PersonDTOV1>> buildPagedModel(Pageable pageable, Page<Person> people) {
        var peopleWithLinks = people.map(person -> {
            var dto = parseObject(person, PersonDTOV1.class);
            addHateoasLinks(dto);
            return dto;
        });

        Link findAllLink = WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder
                                .methodOn(PersonControllerV1.class)
                                .findAll(
                                        pageable.getPageNumber(),
                                        pageable.getPageSize(),
                                        String.valueOf(pageable.getSort()
                                        )
                                ))
                .withSelfRel();

        return assembler.toModel(peopleWithLinks, findAllLink);
    }

    private void addHateoasLinks(PersonDTOV1 dto) {
        dto.add(linkTo(methodOn(PersonControllerV1.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(PersonControllerV1.class).findAll(0, 12, "asc")).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(PersonControllerV1.class).findByName("", 0, 12, "asc")).withRel("findByName").withType("GET"));
        dto.add(linkTo(methodOn(PersonControllerV1.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(PersonControllerV1.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(PersonControllerV1.class).disablePerson(dto.getId())).withRel("disablePerson").withType("PATCH"));
        dto.add(linkTo(methodOn(PersonControllerV1.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        dto.add(linkTo(methodOn(PersonControllerV1.class).exportPage(0, 12, "asc", null)).withRel("exportPage").withType("GET").withTitle("Export People"));
    }
}
