package com.example.elasticsearch.service;

import com.example.elasticsearch.document.Person;
import com.example.elasticsearch.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository repository;

    public void save(final Person person) {
        repository.save(person);
    }

    public Person findById(String id) {
        return repository.findById(id).orElse(null);
    }
}
