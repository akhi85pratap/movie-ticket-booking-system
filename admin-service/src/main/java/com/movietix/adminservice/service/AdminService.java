package com.movietix.adminservice.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.InvalidIsolationLevelException;

import com.movietix.adminservice.dto.AddMovieRequest;
import com.movietix.adminservice.dto.ShowDto;
import com.movietix.adminservice.dto.ValidationDto;
import com.movietix.adminservice.exceptions.InvalidTokenException;
import com.movietix.adminservice.exceptions.ResourceNotFoundException;
import com.movietix.adminservice.feign.AuthClient;
import com.movietix.adminservice.model.Booking;
import com.movietix.adminservice.model.CinemaHall;
import com.movietix.adminservice.model.Movie;
import com.movietix.adminservice.model.Show;
import com.movietix.adminservice.repository.BookingRepository;
import com.movietix.adminservice.repository.CinemaHallRepository;
import com.movietix.adminservice.repository.MovieRepository;
import com.movietix.adminservice.repository.ShowingRepository;

@Service
public class AdminService {
	
	public final DateTimeFormatter showDateTimeFormater = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
	
	private AuthClient authClient;
	private MovieRepository movieRepository;
	private ShowingRepository showingRepository;
	private CinemaHallRepository cinemaHallRepository;
	private BookingRepository bookingRepository;
	private MovieProducer movieProducer;
	
	public AdminService(AuthClient authClient,MovieRepository movieRepository,ShowingRepository showingRepository,
			CinemaHallRepository cinemaHallRepository,BookingRepository bookingRepository,MovieProducer movieProducer) {
		this.authClient=authClient;
		this.movieRepository=movieRepository;
		this.showingRepository=showingRepository;
		this.cinemaHallRepository=cinemaHallRepository;
		this.bookingRepository=bookingRepository;
		this.movieProducer=movieProducer;
	}

	public void updateTicketStatus(String token, String ticketId, String newStatus) {
		if (isAdmin(token)) {
			Booking ticket = bookingRepository.findById(ticketId)
					.orElseThrow(() -> new ResourceNotFoundException("No ticket found with id: " + ticketId));
			ticket.setStatus(newStatus);
			bookingRepository.save(ticket);

		} else
			throw new InvalidTokenException("Only admin can update ticket status");
	}

	public void addMovie(String token, AddMovieRequest request) {
		if (isAdmin(token)) {
			// Create movie entity object from the request
			Movie movie = Movie.builder().id("M" + generateRandomId()).title(request.getTitle())
					.description(request.getDescription()).releaseDate(request.getReleaseDate())
					.runtime(request.getRuntime()).genre(request.getGenre()).language(request.getLanguage())
					.country(request.getCountry()).director(request.getDescription()).cast(request.getCast())
					.rating(request.getRating()).posterUrl(request.getPosterUrl()).trailerUrl(request.getTrailerUrl())
					.build();

			// Save the movie in the database
			movieRepository.save(movie);

			// Create the new movie creation kafka event
			movieProducer.sendMessage("New movie created. Id: " + movie.getId());

			// Foe each show for the movie, create show entity object and save in database
			for (ShowDto dto : request.getShows()) {
				CinemaHall cinemaHall = cinemaHallRepository.findById(dto.getCinemaHallId()).orElseThrow(
						() -> new ResourceNotFoundException("No cinema hall found with id: " + dto.getCinemaHallId()));
				
				
				Show show = Show.builder().id("MT" + generateRandomId()).movie(movie).cinemaHall(cinemaHall)
						.showStartTime(LocalDateTime.parse(dto.getShowStartTime(),showDateTimeFormater))
						.showEndTime(LocalDateTime.parse(dto.getShowEndTime(),showDateTimeFormater))
						.totalSeats(dto.getTotalSeats())
						.bookedSeats(0).build();
				showingRepository.save(show);
			}
		} else
			throw new InvalidIsolationLevelException("Only admin can add new movie");
	}

	public void deleteMovie(String token, String movieId) {
		if (isAdmin(token)) {
			Movie movie = movieRepository.findById(movieId)
					.orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + movieId));
			movieRepository.delete(movie);
		} else
			throw new InvalidTokenException("Only admin can perform delete movie action");
	}

	private boolean isAdmin(String token) {
		ValidationDto authResponse = authClient.validateAuthToken(token);
		return authResponse.isStatus() && authResponse.getRole().equals("ADMIN");
	}

	private String generateRandomId() {
		return UUID.randomUUID().toString();
	}
}
