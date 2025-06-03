package com.movietix.ticketbookingservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.movietix.ticketbookingservice.dto.BookTicketRequest;
import com.movietix.ticketbookingservice.dto.Response;
import com.movietix.ticketbookingservice.dto.ValidationDto;
import com.movietix.ticketbookingservice.exceptions.InvalidTokenException;
import com.movietix.ticketbookingservice.exceptions.ResourceNotFoundException;
import com.movietix.ticketbookingservice.feign.AuthClient;
import com.movietix.ticketbookingservice.model.Show;
import com.movietix.ticketbookingservice.model.User;
import com.movietix.ticketbookingservice.repository.ShowingRepository;
import com.movietix.ticketbookingservice.repository.TicketBookingRepository;
import com.movietix.ticketbookingservice.repository.UserRepository;

class TicketBookingServiceTest {
	@InjectMocks
	private TicketBookingService ticketBookingService;

	@Mock
	private AuthClient authClient;

	@Mock
	private ShowingRepository showingRepository;

	@Mock
	private TicketBookingRepository bookingRepository;

	@Mock
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void bookTicket_ValidRequest_Success() {
		String token = "valid_token";

		BookTicketRequest request = BookTicketRequest.builder().showId("showing_id").seats(3).build();

		ValidationDto authResponse = ValidationDto.builder().status(true).role("CUSTOMER").userId("user_id").build();

		Show show = Show.builder().id("showing_id").totalSeats(10).bookedSeats(2).build();

		User user = User.builder().userId("user_id").build();

		when(authClient.validateAuthToken(token)).thenReturn(authResponse);
		when(showingRepository.findById("showing_id")).thenReturn(Optional.of(show));
		when(userRepository.findById("user_id")).thenReturn(Optional.of(user));

		Response response = ticketBookingService.bookTicket(token, request);

		assertNotNull(response);
		assertEquals("success", response.getStatus());
		assertEquals("Ticket booking successfull", response.getMessage());
		assertEquals(5, show.getBookings().size());
	}

	@Test
	void bookTicket_InvalidToken_ThrowsInvalidTokenException() {
		String token = "invalid_token";
		BookTicketRequest request = new BookTicketRequest();

		ValidationDto authResponse = new ValidationDto();
		authResponse.setStatus(false);

		when(authClient.validateAuthToken(token)).thenReturn(authResponse);

		assertThrows(InvalidTokenException.class, () -> ticketBookingService.bookTicket(token, request));
	}

	@Test
	void bookTicket_NonCustomerRole_ThrowsInvalidTokenException() {
		String token = "valid_token";
		BookTicketRequest request = new BookTicketRequest();

		ValidationDto authResponse = new ValidationDto();
		authResponse.setStatus(true);
		authResponse.setRole("ADMIN");

		when(authClient.validateAuthToken(token)).thenReturn(authResponse);

		assertThrows(InvalidTokenException.class, () -> ticketBookingService.bookTicket(token, request));
	}

	@Test
	void bookTicket_ShowNotFound_ThrowsResourceNotFoundException() {
		String token = "valid_token";
		BookTicketRequest request = new BookTicketRequest();
		request.setShowId("non_existing_showing_id");

		ValidationDto authResponse = new ValidationDto();
		authResponse.setStatus(true);
		authResponse.setRole("CUSTOMER");

		when(authClient.validateAuthToken(token)).thenReturn(authResponse);
		when(showingRepository.findById("non_existing_showing_id")).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> ticketBookingService.bookTicket(token, request));
	}

	@Test
	void bookTicket_NotEnoughSeatsAvailable_ThrowsRuntimeException() {
		String token = "valid_token";
		BookTicketRequest request = new BookTicketRequest();
		request.setShowId("show_id");
		request.setSeats(5);

		ValidationDto authResponse = new ValidationDto();
		authResponse.setStatus(true);
		authResponse.setRole("CUSTOMER");
		authResponse.setUserId("user_id");

		Show show = new Show();
		show.setId("showing_id");
		show.setTotalSeats(10);
		show.setBookedSeats(8);

		User user = new User();
		user.setUserId("user_id");

		when(authClient.validateAuthToken(token)).thenReturn(authResponse);
		when(showingRepository.findById("showing_id")).thenReturn(Optional.of(show));
		when(userRepository.findById("user_id")).thenReturn(Optional.of(user));

		assertThrows(RuntimeException.class, () -> ticketBookingService.bookTicket(token, request));
		assertEquals(8, show.getBookedSeats());
	}

	@Test
	void bookTicket_AuthClientThrowsException_ThrowsException() {
		String token = "valid_token";
		BookTicketRequest request = new BookTicketRequest();

		when(authClient.validateAuthToken(token)).thenThrow(new RuntimeException("Auth client error"));

		assertThrows(RuntimeException.class, () -> ticketBookingService.bookTicket(token, request));
	}

	@Test
	void bookTicket_ShowRepositoryThrowsException_ThrowsException() {
		String token = "valid_token";
		BookTicketRequest request = new BookTicketRequest();
		request.setShowId("show_id");

		ValidationDto authResponse = new ValidationDto();
		authResponse.setStatus(true);
		authResponse.setRole("CUSTOMER");

		when(authClient.validateAuthToken(token)).thenReturn(authResponse);
		when(showingRepository.findById("showing_id")).thenThrow(new RuntimeException("Show repository error"));

		assertThrows(RuntimeException.class, () -> ticketBookingService.bookTicket(token, request));
	}
}
