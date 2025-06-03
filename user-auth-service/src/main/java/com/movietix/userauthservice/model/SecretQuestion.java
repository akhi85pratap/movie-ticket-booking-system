package com.movietix.userauthservice.model;

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

	private String question;
}
