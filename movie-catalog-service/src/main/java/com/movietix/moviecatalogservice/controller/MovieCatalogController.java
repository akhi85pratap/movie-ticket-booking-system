package com.movietix.moviecatalogservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.movietix.moviecatalogservice.dto.AllMovieResponse;
import com.movietix.moviecatalogservice.dto.ErrorResponse;
import com.movietix.moviecatalogservice.dto.MovieDto;
import com.movietix.moviecatalogservice.service.MovieCatalogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Movie Catalog", description = "movie API")
@RestController
@CrossOrigin
public class MovieCatalogController {
	@Autowired
	private MovieCatalogService catalogService;

	@Operation(summary = "View all movies", description = "Get list of currently available movies in theaters")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "All movie retrieve successful", content = @Content(schema = @Schema(implementation = AllMovieResponse.class))) })
	@GetMapping("/all")
	public ResponseEntity<AllMovieResponse> getAllMovies() {
		AllMovieResponse response = catalogService.getAllMovies();
		return ResponseEntity.ok(response);
	}
	
	@Operation(summary = "View movies by city and date", description = "Get list of available movies by city and date")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Available moview for requested city and date are retrieve successful", content = @Content(schema = @Schema(implementation = AllMovieResponse.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@GetMapping("/movies/{movieById}")
	public ResponseEntity<MovieDto> getMoviesByCityAndDate(
			@Parameter(description = "Id of the movie", required = true) @PathVariable String movieById,
			@Parameter(description = "Id of the city", required = true) @RequestParam Long cityId,
			@Parameter(description = "Movie Date", required = true) @RequestParam String date) {
		MovieDto response = catalogService.getMovieByIdAndCityAndDate(movieById, cityId, date);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Search movie by id", description = "Get specific movie details by its id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Movie details retrieve successful", content = @Content(schema = @Schema(implementation = MovieDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@GetMapping("/movies/search/{movieById}")
	public ResponseEntity<MovieDto> getMovieById(
			@Parameter(description = "Id of the movie", required = true) @PathVariable String movieById) {
		MovieDto movie = catalogService.getMovieById(movieById);
		return ResponseEntity.ok(movie);
	}
}
