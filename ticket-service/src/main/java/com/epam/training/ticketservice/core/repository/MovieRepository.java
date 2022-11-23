package com.epam.training.ticketservice.core.repository;

import com.epam.training.ticketservice.core.entity.Movie;
import com.epam.training.ticketservice.core.entity.PriceComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {
    Optional<Movie> findByTitle(String title);

    @Transactional
    @Modifying
    @Query("update Movie m set m.genre = :genre, m.runTime = :runTime where m.title = :title")
    void updateMovie(String title, String genre, Integer runTime);

    @Transactional
    @Modifying
    @Query("update Movie m set m.priceComponent = :component where m = :movie")
    void updateMoviePriceComponent(Movie movie, PriceComponent component);
}
