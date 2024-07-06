package com.jbazann.customers.persistence.repositories;

import com.jbazann.customers.domain.entities.Site;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SiteRepository extends JpaRepository<Site, UUID> {
}
