package com.movietix.ticketbookingservice.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.movietix.ticketbookingservice.model.CinemaSeat;
import com.movietix.ticketbookingservice.model.SeatType;

@Repository
public interface CinemaSeatRepository extends JpaRepository<CinemaSeat, Integer>{

	
	@Query("""
		    SELECT cs
			FROM CinemaSeat cs
			LEFT JOIN ShowSeat ss 
			  ON ss.cinemaSeat.id = cs.id 
			  AND ss.show.id = :showId 
			  AND ss.status IN ('SELECTED', 'SOLD')
			WHERE cs.type = :seatType 
			  AND ss.id IS NULL
		""")
		List<CinemaSeat> findAvailableSeats(
		    @Param("showId") String showId,
		    @Param("seatType") SeatType seatType,
		    Pageable pageable
		);
	
}
