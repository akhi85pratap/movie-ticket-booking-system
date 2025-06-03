package com.movietix.moviecatalogservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movietix.moviecatalogservice.model.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, String>{

}
