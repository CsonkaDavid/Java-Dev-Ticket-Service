package com.epam.training.ticketservice.core.configuration;

import com.epam.training.ticketservice.core.dao.UserDAO;
import com.epam.training.ticketservice.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
@Profile("ci")
@RequiredArgsConstructor
public class InMemoryDatabaseInitializer {
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        UserDAO admin = new UserDAO(null, "admin", "admin", UserDAO.Role.ADMIN);
        userRepository.save(admin);
    }
}
