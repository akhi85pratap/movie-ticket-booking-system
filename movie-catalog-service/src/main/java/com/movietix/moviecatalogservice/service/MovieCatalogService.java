package com.movietix.moviecatalogservice.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.movietix.moviecatalogservice.dto.AllMovieResponse;
import com.movietix.moviecatalogservice.dto.MovieDto;
import com.movietix.moviecatalogservice.dto.ShowDto;
import com.movietix.moviecatalogservice.exceptions.ResourceNotFoundException;
import com.movietix.moviecatalogservice.model.Movie;
import com.movietix.moviecatalogservice.model.Show;
import com.movietix.moviecatalogservice.repository.MovieRepository;
import com.movietix.moviecatalogservice.repository.ShowRepository;

@Service
public class MovieCatalogService {
	
	private final DateTimeFormatter showTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
	
	private MovieRepository movieRepository;
	private ShowRepository showRepository;
	
	public MovieCatalogService(MovieRepository movieRepository, ShowRepository showRepository) {
		this.movieRepository = movieRepository;
		this.showRepository = showRepository;
	}
	
	

	@Transactional(readOnly = true)
	public AllMovieResponse getAllMovies() {
		//Retrieve all movies
		List<Movie> movies = movieRepository.findAll();
		
		//Map every movie object to MovieDto
		List<MovieDto> dto = movies.stream()
				.map(m -> MovieDto.builder().id(m.getId()).title(m.getTitle()).description(m.getDescription())
						.releaseDate(m.getReleaseDate()).runtime(m.getRuntime()).genre(m.getGenre())
						.language(m.getLanguage()).country(m.getCountry()).director(m.getDirector()).cast(m.getCast())
						.rating(m.getRating()).posterUrl(m.getPosterUrl()).trailerUrl(m.getTrailerUrl()).build())
				.collect(Collectors.toList());
		return AllMovieResponse.builder().movies(dto).build();
	}

	@Transactional(readOnly = true)
	public MovieDto getMovieById(String movieId) {
		//Retrieve movie
		Movie movie = movieRepository.findById(movieId)
				.orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + movieId));
		
		//Map every show related to a movie to ShowingDto
		List<ShowDto> shows = movie.getShows().stream()
				.map(m -> ShowDto.builder()
						.id(m.getId())
						.theaterName(m.getCinemaHall().getCinema().getName())
						.location(m.getCinemaHall().getCinema().getLocation())
						.showTime(m.getShowStartTime().format(showTimeFormatter))
						.totalSeats(m.getTotalSeats())
						.bookedSeats(m.getBookedSeats())
						.build())
				.collect(Collectors.toList());
		
		//Map every movie to MovieDto and return result
		return MovieDto.builder().id(movie.getId()).title(movie.getTitle()).description(movie.getDescription())
				.releaseDate(movie.getReleaseDate()).runtime(movie.getRuntime()).genre(movie.getGenre())
				.language(movie.getLanguage()).country(movie.getCountry()).director(movie.getDirector())
				.cast(movie.getCast()).rating(movie.getRating()).posterUrl(movie.getPosterUrl())
				.trailerUrl(movie.getTrailerUrl()).shows(shows).build();
	}
	
	@Transactional(readOnly = true)
	public MovieDto getMovieByIdAndCityAndDate(String movieId, Long cityId, String date) {
		Movie movie = movieRepository.findById(movieId)
				.orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + movieId));
		
		LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		LocalDateTime showStartTime = localDate.atStartOfDay();
		LocalDateTime showEndTime = localDate.atTime(LocalTime.MAX);
		List<Show> upcomingShows = showRepository.findShowsByMovieAndDate(movieId, cityId, showStartTime, showEndTime);
		
		//Map every show related to a movie to ShowingDto
		List<ShowDto> shows = upcomingShows.stream()
				.map(m -> ShowDto.builder()
						.id(m.getId())
						.theaterName(m.getCinemaHall().getCinema().getName())
						.location(m.getCinemaHall().getCinema().getLocation())
						.showTime(m.getShowStartTime().format(showTimeFormatter))
						.totalSeats(m.getTotalSeats())
						.bookedSeats(m.getBookedSeats())
						.build())
				.collect(Collectors.toList());
		
		//Map every movie to MovieDto and return result
		return MovieDto.builder().id(movie.getId()).title(movie.getTitle()).description(movie.getDescription())
				.releaseDate(movie.getReleaseDate()).runtime(movie.getRuntime()).genre(movie.getGenre())
				.language(movie.getLanguage()).country(movie.getCountry()).director(movie.getDirector())
				.cast(movie.getCast()).rating(movie.getRating()).posterUrl(movie.getPosterUrl())
				.trailerUrl(movie.getTrailerUrl()).shows(shows).build();
	}

}
