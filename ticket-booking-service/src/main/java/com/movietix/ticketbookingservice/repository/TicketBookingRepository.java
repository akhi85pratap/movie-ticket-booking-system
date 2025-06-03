package com.movietix.ticketbookingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movietix.ticketbookingservice.model.Booking;

@Repository
public interface TicketBookingRepository extends JpaRepository<Booking, String>{
	
}
