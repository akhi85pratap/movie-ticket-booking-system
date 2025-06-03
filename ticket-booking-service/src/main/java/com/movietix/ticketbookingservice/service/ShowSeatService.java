package com.movietix.ticketbookingservice.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.movietix.ticketbookingservice.dto.BookTicketRequest;
import com.movietix.ticketbookingservice.model.Booking;
import com.movietix.ticketbookingservice.model.CinemaSeat;
import com.movietix.ticketbookingservice.model.SeatStatus;
import com.movietix.ticketbookingservice.model.SeatType;
import com.movietix.ticketbookingservice.model.Show;
import com.movietix.ticketbookingservice.model.ShowSeat;
import com.movietix.ticketbookingservice.repository.CinemaSeatRepository;
import com.movietix.ticketbookingservice.repository.ShowSeatRepository;

@Service
public class ShowSeatService {

	private CinemaSeatRepository cinemaSeatRepository;
	private ShowSeatRepository showSeatRepository; 
	
	public ShowSeatService(CinemaSeatRepository cinemaSeatRepository, ShowSeatRepository showSeatRepository) {
		this.cinemaSeatRepository = cinemaSeatRepository;
		this.showSeatRepository = showSeatRepository;
	}
	
	@Transactional
	public List<ShowSeat> reserveShowSeats(Show show,BookTicketRequest request) {
		List<CinemaSeat> cinemaSeats = cinemaSeatRepository.findAvailableSeats(request.getShowId(), SeatType.valueOf(request.getType()) , PageRequest.of(0, request.getSeats()));
		if(cinemaSeats.isEmpty() || cinemaSeats.size() < request.getSeats()) {
			throw new RuntimeException("Seats not available");
		}
		
		List<ShowSeat> showSeats = cinemaSeats.stream().map(cinemaSeat -> ShowSeat.builder()
				.cinemaSeat(cinemaSeat)
				.price(cinemaSeat.getPrice())
				.show(show)
				.status(SeatStatus.SELECTED)
				.build()
				).toList();
		
		return showSeatRepository.saveAll(showSeats);
	}
	
	@Transactional
	public List<ShowSeat> confirmShowSeats(List<ShowSeat> showSeats, Booking booking ) {

		List<ShowSeat> soldSeats = showSeats.stream().map(showSeat -> {
			showSeat.setStatus(SeatStatus.SOLD);
			showSeat.setBooking(booking);
			return showSeat;
		}).toList();
		
		return showSeatRepository.saveAll(soldSeats);
	}
}
