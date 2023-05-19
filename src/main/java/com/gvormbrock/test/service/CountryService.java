package com.gvormbrock.test.service;

import com.gvormbrock.test.model.Country;
import com.gvormbrock.test.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;


    public Optional<Country> findByName(String name) {
        return countryRepository.findByName(name);
    }

    public Optional<Country> findByCountryCode(String countryCode) {
        return countryRepository.findByCountryCode(countryCode);
    }

    public Optional<Country> findById(Long id) {
        return countryRepository.findById(id);
    }

    public void save(Country country) {
        countryRepository.save(country);
    }
}
