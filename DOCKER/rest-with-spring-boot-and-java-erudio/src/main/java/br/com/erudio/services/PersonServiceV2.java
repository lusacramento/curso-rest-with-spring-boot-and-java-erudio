package br.com.erudio.services;

import br.com.erudio.data.dto.v2.PersonDTOV2;
import br.com.erudio.exception.ResourceNotFoundException;
import br.com.erudio.model.Person;
import br.com.erudio.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import static br.com.erudio.mapper.ObjectMapper.parseListObjects;
import static br.com.erudio.mapper.ObjectMapper.parseObject;

@Service
public class PersonServiceV2 {

    private final AtomicLong counter = new AtomicLong();
    private Logger logger = Logger.getLogger(PersonServiceV1.class.getName());

    @Autowired
    PersonRepository repository;

    public List<PersonDTOV2> findAll(){
        logger.info("Finding people...");
        return parseListObjects(repository.findAll(), PersonDTOV2.class);
    }

    public PersonDTOV2 findById(Long id){
        logger.info("Finding one Person...");

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this Id"));
        return parseObject(entity, PersonDTOV2.class);
    }


    public PersonDTOV2 create(PersonDTOV2 person) {
        logger.info("Creating person for \\person.getFirstName()...");
        var entity = parseObject(person, Person.class);

        return parseObject(repository.save(entity), PersonDTOV2.class);
    }

    public PersonDTOV2 update(PersonDTOV2 person){
        logger.info("Updating person...");

        Person entity = repository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this Id"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        return parseObject(repository.save(entity), PersonDTOV2.class);
    }

    public void delete(Long id){
        logger.info("Deleting one Person...");

        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this Id"));

        repository.delete(entity);
    }
}
