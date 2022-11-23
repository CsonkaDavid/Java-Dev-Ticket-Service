package com.epam.training.ticketservice.core.service;

import com.epam.training.ticketservice.core.repository.BasePriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasePriceServiceImpl implements BasePriceService {

    private final BasePriceRepository basePriceRepository;

    @Override
    public void updateBasePrice(int amount) {
        basePriceRepository.updateBasePrice(amount);
    }
}
