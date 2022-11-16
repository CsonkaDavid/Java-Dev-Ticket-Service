package com.epam.training.ticketservice.core.repository;

import com.epam.training.ticketservice.core.dao.UserDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserDAO, Integer> {

}
