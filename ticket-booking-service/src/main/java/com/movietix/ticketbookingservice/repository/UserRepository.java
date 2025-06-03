package com.movietix.ticketbookingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movietix.ticketbookingservice.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
