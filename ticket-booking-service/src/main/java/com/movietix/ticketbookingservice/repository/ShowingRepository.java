package com.movietix.ticketbookingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movietix.ticketbookingservice.model.Show;

@Repository
public interface ShowingRepository extends JpaRepository<Show, String>{

}
