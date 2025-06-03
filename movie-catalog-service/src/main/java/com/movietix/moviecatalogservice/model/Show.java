package com.movietix.moviecatalogservice.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_shows")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Show {
	
	@Id
	private String id;

    private LocalDateTime showStartTime;
    private LocalDateTime showEndTime;
    private int bookedSeats;
    private int totalSeats;

    @ManyToOne
    @JoinColumn(name = "hall_id")
    private CinemaHall cinemaHall;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @OneToMany(mappedBy = "show")
    private List<ShowSeat> showSeats;

    @OneToMany(mappedBy = "show")
    private List<Booking> bookings;

}
