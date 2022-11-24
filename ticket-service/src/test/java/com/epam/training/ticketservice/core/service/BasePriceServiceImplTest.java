package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.repository.BasePriceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BasePriceServiceImplTest {

    @Mock
    private BasePriceRepository basePriceRepository;

    @InjectMocks
    private BasePriceServiceImpl testBasePriceService;

    @Test
    void testUpdateBasePriceShouldUpdateBasePriceWhenInputIsValid() {
        //Given
        int newPrice = 1200;

        //When
        testBasePriceService.updateBasePrice(newPrice);

        //Then
        Mockito.verify(basePriceRepository).updateBasePrice(newPrice);
    }
}