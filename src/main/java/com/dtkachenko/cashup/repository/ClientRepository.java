package com.dtkachenko.cashup.repository;


import com.dtkachenko.cashup.model.Client;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "clients", path = "clients")
public interface ClientRepository extends CrudRepository<Client, Long> {

    List<Client> findAll();

    List<Client> findByFirstName(@Param("name") String name);

    Client save(Client client);
}
