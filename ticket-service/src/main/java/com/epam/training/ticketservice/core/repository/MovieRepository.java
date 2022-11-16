package com.epam.training.ticketservice.core.repository;

import com.epam.training.ticketservice.core.dao.MovieDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<MovieDAO, Integer> {
    Optional<MovieDAO> findByTitle(String title);

    @Modifying
    @Query("update movie set genre = :newGenre, run_time = :newRunTime where title = :title")
    void updateMovie(String title, String newGenre, Integer newRunTime);

    void deleteByTitle(String title);
}
