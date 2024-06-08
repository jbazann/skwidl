package com.jbazann.customers.persistence.repositories;

import com.jbazann.customers.domain.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
}
