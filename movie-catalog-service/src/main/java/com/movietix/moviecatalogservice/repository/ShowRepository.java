package com.movietix.moviecatalogservice.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.movietix.moviecatalogservice.model.Show;

@Repository
public interface ShowRepository extends JpaRepository<Show, String>{

	@Query("""
		    SELECT s FROM Show s
		    WHERE s.movie.id = :movieId
		      AND s.showStartTime BETWEEN :startOfDay AND :endOfDay
		      AND s.cinemaHall.cinema.city.id = :cityId
		""")
		List<Show> findShowsByMovieAndDate(
		    @Param("movieId") String movieId,
		    @Param("cityId") Long cityId,
		    @Param("startOfDay") LocalDateTime startOfDay,
		    @Param("endOfDay") LocalDateTime endOfDay
		);
}
