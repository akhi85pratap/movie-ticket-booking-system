package com.movietix.adminservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movietix.adminservice.model.Show;

@Repository
public interface ShowingRepository extends JpaRepository<Show, String> {

}
