package com.movietix.ticketbookingservice.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.movietix.ticketbookingservice.dto.BookTicketRequest;
import com.movietix.ticketbookingservice.dto.Response;
import com.movietix.ticketbookingservice.dto.ValidationDto;
import com.movietix.ticketbookingservice.exceptions.InvalidTokenException;
import com.movietix.ticketbookingservice.exceptions.ResourceNotFoundException;
import com.movietix.ticketbookingservice.feign.AuthClient;
import com.movietix.ticketbookingservice.model.Booking;
import com.movietix.ticketbookingservice.model.Show;
import com.movietix.ticketbookingservice.model.ShowSeat;
import com.movietix.ticketbookingservice.model.User;
import com.movietix.ticketbookingservice.repository.ShowingRepository;
import com.movietix.ticketbookingservice.repository.TicketBookingRepository;
import com.movietix.ticketbookingservice.repository.UserRepository;

@Service
public class TicketBookingService {
	@Autowired
	private AuthClient authClient;
	@Autowired
	private ShowingRepository showingRepository;
	@Autowired
	private TicketBookingRepository bookingRepository;
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ShowSeatService showSeatService;


	@Transactional
	public Response bookTicket(String token, BookTicketRequest request) {
		String invalidMsg = "Invalid Token";
		try {
			ValidationDto authResponse = authClient.validateAuthToken(token);
			if (authResponse.isStatus()) {

				// Only CUSTOMER can perform the book ticket operation
				if (authResponse.getRole().equals("CUSTOMER")) {

					// Retrieve the show details
					Show show = showingRepository.findById(request.getShowId()).orElseThrow(
							() -> new ResourceNotFoundException("No show found with id: " + request.getShowId()));

					// Find the remaining seats available for the show
					int remainingSeats = show.getTotalSeats() - show.getBookedSeats();

					// If requested seat count is less than or equal to remaining seats
					if (request.getSeats() <= remainingSeats) {
	
						//Reserve seats if available
						List<ShowSeat> showSeats = showSeatService.reserveShowSeats(show, request);
						
						show.setBookedSeats(show.getBookedSeats() + request.getSeats());

						// Retrieve the user details
						User user = userRepository.findById(authResponse.getUserId()).orElse(null);

						Booking ticket = Booking.builder().id(generateRandomId()).show(show).user(user)
								.numSeats(request.getSeats()).status("CONFIRM").build();
						
						showSeatService.confirmShowSeats(showSeats, ticket);
						// Save the ticket and show record
						ticket.setShowSeats(showSeats);
						bookingRepository.save(ticket);
						
						showingRepository.save(show);
						
						// Return response
						return Response.builder().status("success").message("Ticket booking successfull").build();
					} else
						throw new RuntimeException("Ticket not available!");
				} else
					throw new InvalidTokenException("Only a customer can perform ticket book");
			} else
				throw new InvalidTokenException(invalidMsg);
		} catch (Exception e) {
			throw e;
		}
	}

	private String generateRandomId() {
		return "TB" + UUID.randomUUID().toString();
	}

}
