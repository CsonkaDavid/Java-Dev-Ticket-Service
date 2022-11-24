package com.epam.training.ticketservice.core.configuration;

import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class AdminInitializer {
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        if (userRepository.findAll().isEmpty()) {
            User admin = new User(null, "admin", "admin", User.Role.ADMIN);
            userRepository.save(admin);
        }
    }
}