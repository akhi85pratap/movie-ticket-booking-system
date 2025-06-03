package com.movietix.ticketbookingservice.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_ticket_booking")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking {
	@Id
	private String id;
	
	@Column(name = "num_seats")
	private int numSeats;
	
	@Column(name = "ticket_seats")
	private String status;
	
	@CreationTimestamp
    private LocalDateTime timestamp;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "show_id")
	private Show show;
	
	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShowSeat> showSeats;
	
	@OneToOne(mappedBy = "booking")
	private Payment payment;

}
