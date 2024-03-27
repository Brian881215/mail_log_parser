package com.taiwanmobile.socialengineer.smstest;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class ParseWritingLine {
	private ArrayList<String> queueIdArrayList = new ArrayList<>();
	
	public void findMessageId(String line) {
		if(line == null) {
			return;
		}
		String messageIDRegix = "message-id=<(.*?)>";
		String queueIDRegix = ".*: (\\w{10,20}):.*";
		Pattern pattern = Pattern.compile(messageIDRegix);
	    Pattern queueIdPattern = Pattern.compile(queueIDRegix);
		Matcher matcher = pattern.matcher(line);
	    Matcher queueIdMatcher = queueIdPattern.matcher(line);
	    if (matcher.find()) { //這個是在做把queueId存進去我們預設的串列中
	        String messageID = matcher.group(1);
	        // do something with the message ID
	        System.out.println("Found message ID: " + messageID);
	        if (queueIdMatcher.matches()) {
                String queueId = queueIdMatcher.group(1);
                queueIdArrayList.add(queueId);
            }
	    }
	}
	public void findDSN(String line) {
		if(line == null) {
			return;
		}
		String queueIDRegix = ".*: (\\w{10,20}):.*";
		String dsnRegix = "dsn=(\\d\\.\\d\\.\\d),";
	    Pattern queueIdPattern = Pattern.compile(queueIDRegix);
	    Pattern dsnPattern = Pattern.compile(dsnRegix);
	    String queueId = "";
	    Matcher queueIdMatcher = queueIdPattern.matcher(line);
	    if(queueIdMatcher.find()) {
	    	queueId = queueIdMatcher.group(1);
	    }
		if(queueIdArrayList.contains(queueId)) {
			Matcher matcher = dsnPattern.matcher(line);
			if(matcher.find()) {
				System.out.println("Find DSN: "+matcher.group(1));
			}
		}
	} 
}
