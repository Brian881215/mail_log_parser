package com.taiwanmobile.socialengineer.smstest;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostfixEmailJpaRepository  extends JpaRepository<PostfixEmail, Integer>{

	Optional<PostfixEmail> findByPostfixEmailId(Integer postfixEmailId);
	Optional<PostfixEmail> findByMessageId(String messageId);
	Optional<PostfixEmail> findByQueueId(String queueId);
}

