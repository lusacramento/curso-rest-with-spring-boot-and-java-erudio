package br.com.erudio.controllers;

import br.com.erudio.controllers.docs.PersonControllerV1Docs;
import br.com.erudio.data.dto.v1.PersonDTOV1;
import br.com.erudio.exception.handler.FileNotFoundException;
import br.com.erudio.file.exporter.MediaTypes;
import br.com.erudio.services.PersonServiceV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;


//@CrossOrigin all domains
//@CrossOrigin("http://localhost:8080") specific domain
//@CrossOrigin({"http://localhost:8080"}) multiple domains

@RestController
@RequestMapping("/api/v1/person")
@Tag(name = "People", description = "Endpoints for Managing People")
public class PersonControllerV1 implements PersonControllerV1Docs {

    @Autowired
    private PersonServiceV1 service;

    @GetMapping(produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE
    })

    @Override
    public ResponseEntity<PagedModel<EntityModel<PersonDTOV1>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction

    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping(
            value = "findPeopleByName/{firstName}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })

    @Override
    public ResponseEntity<PagedModel<EntityModel<PersonDTOV1>>> findByName(
            @PathVariable("firstName") String firstName,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction

    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
        return ResponseEntity.ok(service.findByName(firstName, pageable));
    }

    @GetMapping(
            value = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            }
    )
    @Override
    public PersonDTOV1 findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @GetMapping(
            value = "/export/{id}",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    @Override
    public ResponseEntity<Resource> export(@PathVariable("id") Long id, HttpServletRequest request) {
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);

        Resource file = service.exportPerson(id, acceptHeader);

        String fileName = "person.pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(acceptHeader))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(file);
    }

    //    @CrossOrigin({"http://localhost:8080"}) local cors
    @PostMapping(
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            }
    )

    @Override
    public ResponseEntity<PersonDTOV1> create(@RequestBody PersonDTOV1 person) {
        PersonDTOV1 savedUser = service.create(person);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedUser);
    }

    @GetMapping(value = "/exportPage",
            produces = {
                    MediaTypes.APPLICATION_CSV_VALUE,
                    MediaTypes.APPLICATION_XLSX_VALUE,
                    MediaTypes.APPLICATION_PDF_VALUE,
            })
    @Override
    public ResponseEntity<Resource> exportPage(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                               @RequestParam(value = "size", defaultValue = "12") Integer size,
                                               @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                               HttpServletRequest request
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));

        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);

        Resource file = service.exportPage(pageable, acceptHeader);

        Map<String, String> extentionMap = Map.of(
                MediaTypes.APPLICATION_XLSX_VALUE, ".xlsx",
                MediaTypes.APPLICATION_CSV_VALUE, ".csv",
                MediaTypes.APPLICATION_PDF_VALUE, ".pdf"
        );

        var fileExtension = extentionMap.getOrDefault(acceptHeader, "");
        var contentType = acceptHeader != null ? acceptHeader : "application/octet-stream";
        var fileName = "people_exported" + fileExtension;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(file);
    }

    @PostMapping(
            value = "massCreation",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            }
    )
    @Override
    public List<PersonDTOV1> massCreation(@RequestParam("file") MultipartFile file) {
        return service.massCreation(file);
    }

    @PutMapping(
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            }
    )

    @Override
    public PersonDTOV1 update(@RequestBody PersonDTOV1 person) {
        return service.update(person);
    }

    @PatchMapping(
            value = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            }
    )
    @Override
    public PersonDTOV1 disablePerson(Long id) {
        return service.disablePerson(id);
    }

    @DeleteMapping(value = "/{id}")

    @Override
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

