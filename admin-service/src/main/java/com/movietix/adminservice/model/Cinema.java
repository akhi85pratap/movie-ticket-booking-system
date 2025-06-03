package com.movietix.adminservice.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "tb_cinema")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cinema {
	@Id
	private String id;

	@Column(nullable = false)
	private String name;
	
	@Column(name="total_cinema_halls")
	private Integer totalCinemaHalls;

	@Column(nullable = false)
	private String location;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "city_id")
	private City city;

	@OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CinemaHall> cinemaHall;
}
