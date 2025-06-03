package com.movietix.userauthservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.movietix.userauthservice.model.SecretQuestion;

public interface SecretQuestionRepository extends JpaRepository<SecretQuestion, Long> {

}
