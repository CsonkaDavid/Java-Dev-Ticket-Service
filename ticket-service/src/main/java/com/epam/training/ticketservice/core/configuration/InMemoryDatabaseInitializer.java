package com.epam.training.ticketservice.core.configuration;

import com.epam.training.ticketservice.core.entity.User;
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
        User admin = new User(null, "admin", "admin", User.Role.ADMIN);
        userRepository.save(admin);
    }
}
