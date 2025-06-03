package com.movietix.moviecatalogservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShowDto {
	private String id;
	private String theaterName;
	private String location;
	private String showTime;
	private int totalSeats;
	private int bookedSeats;
}
