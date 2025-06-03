package com.movietix.adminservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movietix.adminservice.model.CinemaHall;

@Repository
public interface CinemaHallRepository extends JpaRepository<CinemaHall, Long> {

	List<CinemaHall> findByCinemaId(String cinemaId);
	
}
