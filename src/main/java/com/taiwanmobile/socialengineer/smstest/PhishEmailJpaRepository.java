package com.taiwanmobile.socialengineer.smstest;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface PhishEmailJpaRepository extends JpaRepository<PhishEmail, Integer>{
	Optional<PhishEmail> findByPhishEmailId(Integer phsihEmailId);
	Optional<PhishEmail> findByMessageId(String messageId);
	Optional<PhishEmail> findByPostfixQueueId(String queueId);
}
