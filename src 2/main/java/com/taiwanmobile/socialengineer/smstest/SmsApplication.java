package com.taiwanmobile.socialengineer.smstest;
	

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;


import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
@Slf4j
public class SmsApplication implements CommandLineRunner  {
	
	@Autowired
	CommonService commonService;

	@Autowired
	ParseWritingLine parseWritingLine;
	 
	 
	@Value("${email.log.file}")
	 private String emailLogFile;
	
	public static void main(String[] args) {
		SpringApplication.run(SmsApplication.class, args);
	}
	
	
    @Override
    public void run(String... args) throws Exception {
    	System.out.println("ddde");
//    	log.info("Send sms test");
//    	commonService.sendTwmSms("0930160401", "Test Semd short Sms from cloud run");
//    	
//    	commonService.sendTwmSms("0930160401", "Test Semd long Sms from cloud run Test Semd long Sms from cloud run Test Semd long Sms from cloud run Test Semd long Sms from cloud run Test Semd long Sms from cloud run Test Semd long Sms from cloud run Test Semd long Sms from cloud run Test Semd long Sms from cloud run Test Semd long Sms from cloud run");
    	boolean running = true;
    	BufferedInputStream reader = new BufferedInputStream(new FileInputStream(emailLogFile));
    	BufferedReader r = new BufferedReader(
    	        new InputStreamReader(reader, StandardCharsets.UTF_8));
    	String  thisLine = null;
    	while (running) {
    		while ((thisLine = r.readLine()) != null) {
//	            System.out.println(thisLine);
    			parseWritingLine.findMessageId(thisLine);
    			parseWritingLine.findDSN(thisLine);
	           }
//    		if (reader.available() > 0) {
//    			
//    			String temp = r.readLine();
//    			System.out.print(temp);
//    			parseWritingLine.findMessageId(temp);
//                parseWritingLine.findDSN(temp);
//    		}else {
//    			try {
//    				Thread.sleep(500);
//    			} catch (InterruptedException ex) {
//    				running = false;
//    				reader.close();
//    			}
//    		}
    	
    	}

}


}


//
//public static void main(String[] args) throws IOException {
//	  boolean running = true;
//	  BufferedInputStream bufferInputStream = new BufferedInputStream(new FileInputStream("/Users/yanyouxuan/Desktop/streamingtest.txt"));
//	  BufferedReader reader = new BufferedReader(new InputStreamReader(bufferInputStream, StandardCharsets.UTF_8));
//	  String  thisLine = null;
//	  
//	  while (running) {
//	          while ((thisLine = reader.readLine()) != null) {
//	              System.out.println(thisLine);
//	           }
//	  }
