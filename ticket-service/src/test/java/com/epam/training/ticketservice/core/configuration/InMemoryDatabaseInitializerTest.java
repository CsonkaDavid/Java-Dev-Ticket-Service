package com.epam.training.ticketservice.core.configuration;

import com.epam.training.ticketservice.core.entity.User;
import com.epam.training.ticketservice.core.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class InMemoryDatabaseInitializerTest {

    private final UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);
    private final InMemoryDatabaseInitializer testInMemoryDBInitializer =
            new InMemoryDatabaseInitializer(userRepositoryMock);

    private final User admin = new User(null, "admin", "admin", User.Role.ADMIN);

    @Test
    void testInMemoryDatabaseAdminSaving() {
        //Given
        Mockito.when(userRepositoryMock.save(admin)).thenReturn(admin);

        //When
        testInMemoryDBInitializer.init();

        //Then
        Mockito.verify(userRepositoryMock).save(admin);
    }
}