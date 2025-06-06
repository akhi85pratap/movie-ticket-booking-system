package com.movietix.adminservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movietix.adminservice.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

}
