package com.taiwanmobile.socialengineer.smstest;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;


@Slf4j
@Transactional(isolation = Isolation.SERIALIZABLE)
@Service
public class ParseWritingLineOrg {
	@Autowired
    private PhishEmailJpaRepository phishEmailJpaRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(ParseWritingLine.class);
//	private ArrayList<String> queueIdArrayList = new ArrayList<>();
	
	public String findQueueIdByMessageId(String line) {
		
		if(line == null) {
			return null;
		}
//		String messageIDRegix = "message-id=<(.*?)>";
		String messageIDRegix = "message-id\\s*=\\s*(?:<([^>]+)>|([^;]+))";
		
		String queueIDRegix = ".*: (\\w{10,20}):.*";
		Pattern pattern = Pattern.compile(messageIDRegix);
	    Pattern queueIdPattern = Pattern.compile(queueIDRegix);
		Matcher matcher = pattern.matcher(line);
	    Matcher queueIdMatcher = queueIdPattern.matcher(line);
	    if (matcher.find()) { //這個是在做把queueId存進去我們預設的串列中
//	        String messageID = matcher.group(1);
	    	String messageID = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
	        // do something with the message ID
//	        System.out.println("Found message ID: " + messageID);
	        logger.info("Found message ID: " +messageID);
	        Optional<PhishEmail>  phishEmailLog = phishEmailJpaRepository.findByMessageId(messageID);   
	        if (!phishEmailLog.isPresent()) {
//				throw new ResourceNotFoundException(ApiErrorCode.MESSAGE_ID_NOT_FOUND, "找不到message id");
				return null;
	        }
	        PhishEmail phishEmail = phishEmailLog.get();
		    String dbMessageId = phishEmailLog.get().getMessageId();
		    logger.info("line 45 testing, print db messageId:"+dbMessageId);
		    if(dbMessageId.equals(messageID)) {
		    	if(queueIdMatcher.matches()) {
		    		String queueId = queueIdMatcher.group(1) != null ? queueIdMatcher.group(1) : queueIdMatcher.group(2);
//	                String queueId = queueIdMatcher.group(1);
//	                queueIdArrayList.add(queueId);
	                logger.info("Set Queue ID in line49: " +queueId);
	                phishEmail.setPostfixQueueId(queueId);
	                //phishEmailLog.get().setPostfixQueueId(queueId);
	                phishEmailJpaRepository.saveAndFlush(phishEmail);//這行沒有加的話會無法寫進去資料庫
	            }
		    	return messageID;
		    }	      
	    }
	    return null;
	}
	
	public String findDSN(String line) {
		if(line == null) {
			return null;
		}
		String queueIDRegix = ".*: (\\w{10,20}):.*";
		String dsnRegix = "dsn=(\\d\\.\\d\\.\\d),";
	    Pattern queueIdPattern = Pattern.compile(queueIDRegix);
	    Pattern dsnPattern = Pattern.compile(dsnRegix);
	    String queueId = "";
	    Matcher queueIdMatcher = queueIdPattern.matcher(line);
	    if(queueIdMatcher.find()) {
	    	queueId = queueIdMatcher.group(1);
	    	logger.info("Find Queue ID in func findDSN: " +queueId);
	    
		    Optional<PhishEmail>  phishEmailLog = phishEmailJpaRepository.findByPostfixQueueId(queueId);
		    if (!phishEmailLog.isPresent()) {
//				throw new ResourceNotFoundException(ApiErrorCode.QUEUE_ID_NOT_FOUND, "找不到queue id");
				return null;
		    }
		    PhishEmail phishEmail = phishEmailLog.get();
		    String dbQueueId = phishEmail.getPostfixQueueId();
		    logger.info("line 83 testing, print db postfixQueueId:"+dbQueueId);
			Matcher matcher = dsnPattern.matcher(line);
			if(matcher.find()) {
				String dsn = matcher.group(1);
				logger.info("Found DSN: " +dsn);
//				System.out.println("Find DSN: "+dsn);
				phishEmail.setPostfixDsnCode(dsn);
				phishEmailJpaRepository.saveAndFlush(phishEmail);//這行沒有加的話會無法寫進去資料庫
				return dsn;
			}
	    }
		return null;
		
	} 
}
