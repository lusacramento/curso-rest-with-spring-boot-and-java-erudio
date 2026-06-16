package br.com.erudio.controllers;

import br.com.erudio.data.dto.v2.PersonDTOV2;
import br.com.erudio.services.PersonServiceV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v2/person")
public class PersonControllerV2 {

    @Autowired
    private PersonServiceV2 service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonDTOV2> findAll(){
        return service.findAll();
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PersonDTOV2 findById(@PathVariable("id") Long id){
        return service.findById(id);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public ResponseEntity<PersonDTOV2> create(@RequestBody PersonDTOV2 person){
        PersonDTOV2 savedUser = service.create(person);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedUser);
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PersonDTOV2 update(@RequestBody PersonDTOV2 person){
        return service.update(person);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
