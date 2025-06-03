package com.movietix.adminservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ShowDto {
	@NotBlank(message = "Ciname id is required")
	private String cinemaId;
	@NotBlank(message = "Ciname Hall id is required")
	private Long cinemaHallId;
	@NotBlank(message = "showTime is required")
	private String showStartTime;
	@NotBlank(message = "showTime is required")
	private String showEndTime;
	@Min(value = 50)
	private int totalSeats;
}
