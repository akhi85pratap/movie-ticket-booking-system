package com.movietix.ticketbookingservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_secret_questions")
@Data
public class SecretQuestion {
	@Id
	private Long id;

	@Column(nullable = false)
	private String question;
}
