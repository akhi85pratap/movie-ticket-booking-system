package com.movietix.ticketbookingservice.dto;

import com.movietix.ticketbookingservice.config.ValidEnum;
import com.movietix.ticketbookingservice.model.SeatType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookTicketRequest {
	@NotBlank(message = "Show id is required")
	private String showId;
	
	@NotNull(message = "No of seats to book is required")
	@Max(value = 6,message = "One cannot book more then 6 tickets for a show")
	@Min(value = 1,message = "Ateast one ticket can be booked")
	private Integer seats;
	
	@ValidEnum(enumClass = SeatType.class, message = "Invalid value, Please select REGULAR/PREMIUM")
	private String type;
}
