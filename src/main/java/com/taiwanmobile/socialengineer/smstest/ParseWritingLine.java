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
public class ParseWritingLine {
	
	@Autowired
    private PostfixEmailJpaRepository postfixEmailJpaRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(ParseWritingLine.class);
	
	public String findQueueIdByMessageId(String line) {
		
		if(line == null) {
			return null;
		}

		String messageIDRegix = "message-id\\s*=\\s*(?:<([^>]+)>|([^;]+))";
		String queueIDRegix = ".*: (\\w{10,20}):.*";
		Pattern pattern = Pattern.compile(messageIDRegix);
	    Pattern queueIdPattern = Pattern.compile(queueIDRegix);
		Matcher matcher = pattern.matcher(line);
	    Matcher queueIdMatcher = queueIdPattern.matcher(line);
	    if (matcher.find()) { //這個是在做把queueId存進去我們預設的串列中

	    	String messageID = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
	        logger.info("Found message ID: " +messageID);
	        Optional<PostfixEmail>  postfixEmailLog = postfixEmailJpaRepository.findByMessageId(messageID);   
	        if (postfixEmailLog.isPresent()) {
	        	//如果messageId存在在資料表裡面的話，表示之前已經有寫入進去messageId 和queueId了
	        	//因為一次都是兩個加進去，表示正確的queueid 與 messageid有存進去
				return null;
	        }
	        PostfixEmail postfixEmail = new PostfixEmail();
	        postfixEmail.setMessageId(messageID);
		   
	    	if(queueIdMatcher.matches()) {
	    		String queueId = queueIdMatcher.group(1) != null ? queueIdMatcher.group(1) : queueIdMatcher.group(2);
                logger.info("Set Queue ID in line49: " +queueId);
                postfixEmail.setQueueId(queueId);
                postfixEmailJpaRepository.saveAndFlush(postfixEmail);//這行沒有加的話會無法寫進去資料庫
	         }
		    return messageID;
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
	    
		    Optional<PostfixEmail>  postfixEmailLog = postfixEmailJpaRepository.findByQueueId(queueId);
		    if (!postfixEmailLog.isPresent()) {
				return null;
		    }
		    PostfixEmail postfixEmail = postfixEmailLog.get();
		    String dbQueueId =  postfixEmail.getQueueId();//取得資料庫裡面你要抓的queueid
		    logger.info("line 83 testing, print db postfixQueueId:"+dbQueueId);
			Matcher matcher = dsnPattern.matcher(line);
			if(matcher.find()) {
				String dsn = matcher.group(1);
				logger.info("Found DSN: " +dsn);
				postfixEmail.setDsn(dsn);
				postfixEmailJpaRepository.saveAndFlush(postfixEmail);//這行沒有加的話會無法寫進去資料庫
				return dsn;
			}
	    }
	    return null;
		
	} 
}
