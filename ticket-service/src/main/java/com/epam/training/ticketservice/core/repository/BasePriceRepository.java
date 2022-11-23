package com.epam.training.ticketservice.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BasePriceRepository
        extends JpaRepository<com.epam.training.ticketservice.core.entity.BasePrice, Integer> {

    @Transactional
    @Modifying
    @Query("update BasePrice r set r.amount = :newAmount where r.id = 1")
    void updateBasePrice(int newAmount);
}
