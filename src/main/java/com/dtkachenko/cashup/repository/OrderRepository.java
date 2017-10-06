package com.dtkachenko.cashup.repository;


import com.dtkachenko.cashup.model.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "orders", path = "orders")
public interface OrderRepository extends CrudRepository<Order, Long> {

    List<Order> findAll();

    List<Order> findByOrderDate(@Param("orderDate") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate orderDate);
}
