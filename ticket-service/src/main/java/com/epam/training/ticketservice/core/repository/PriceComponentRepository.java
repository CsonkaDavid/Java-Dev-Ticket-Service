package com.epam.training.ticketservice.core.repository;

import com.epam.training.ticketservice.core.entity.PriceComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriceComponentRepository extends JpaRepository<PriceComponent, Integer> {
    Optional<PriceComponent> findPriceComponentByName(String name);
}
