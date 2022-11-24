package com.epam.training.ticketservice.core.configuration;

import com.epam.training.ticketservice.core.entity.BasePrice;
import com.epam.training.ticketservice.core.repository.BasePriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class BasePriceInitializer {
    private final BasePriceRepository basePriceRepository;

    @PostConstruct
    public void init() {
        if (basePriceRepository.findAll().isEmpty()) {
            BasePrice basePrice = new BasePrice(1, 1500);
            basePriceRepository.save(basePrice);
        }
    }
}

