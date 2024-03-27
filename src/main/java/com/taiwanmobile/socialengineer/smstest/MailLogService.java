package com.taiwanmobile.socialengineer.smstest;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
public class MailLogService {
	@Autowired
    private PhishEmailJpaRepository phishEmailJpaRepository;

    public void saveMailLog(Integer phishEmailId) {
        PhishEmail phishEmail = new PhishEmail();
    
        Optional<PhishEmail>  phishEmailLog = phishEmailJpaRepository.findByPhishEmailId(phishEmailId);
        String messageId = phishEmailLog.get().getMessageId();
        
    }
}
