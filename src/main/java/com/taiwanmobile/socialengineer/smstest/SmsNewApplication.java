package com.taiwanmobile.socialengineer.smstest;
	

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
@EnableTransactionManagement
@SpringBootApplication
@Slf4j
public class SmsNewApplication implements CommandLineRunner  {
	
	@Autowired
	CommonService commonService;

	@Autowired
	ParseWritingLine parseWritingLine;
	
	@Value("${email.log.file}")
	 private String emailLogFile;
	
	public static void main(String[] args) {
		SpringApplication.run(SmsNewApplication.class, args);
	}
	
    @Override
    public void run(String... args) throws Exception {
    
    	Path logFilePath = Paths.get(emailLogFile);
    	long lastPosition = 0;
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            logFilePath.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            boolean running = true;
            while (running) {
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException e) {
                    break;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    	
                        Path modifiedFilePath = (Path) event.context();

                        if (modifiedFilePath.equals(logFilePath.getFileName())) {
                 
                            // 執行相應的處理邏輯，例如讀取郵件日誌中的新資料
                        	try(RandomAccessFile file = new RandomAccessFile(logFilePath.toFile(), "r")){
								file.seek(lastPosition);
								String line;
                                while ((line = file.readLine()) != null) {

                                    System.out.println("Log entry: " + line);
                                    // 執行相應的處理邏輯，例如解析郵件日誌中的新資料
                                	String myMessageId =  parseWritingLine.findQueueIdByMessageId(line);
                                	String myDsn =  parseWritingLine.findDSN(line);
                                	
                                	callPostMethod(myMessageId,myDsn,"phish","BrianTesting");
                                	
                                    lastPosition = file.getFilePointer(); // 更新上一次讀取的位置，很重要
                                }
                        	}catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                key.reset();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    	
    public static void callPostMethod(String messageId,String dsn,String type,String token) throws Exception {
        // Create an HttpClient instance
        HttpClient httpClient = HttpClients.createDefault();

        // Create an HttpPost request with the target API URL
        HttpPost httpPost = new HttpPost("https://twmsocialengineeringt.com/platform/phish-email-status/");
        //HttpPost httpPost = new HttpPost("https://localhost:8080/platform/phish-email-status/");

        // Set request headers
        httpPost.setHeader("Content-Type", "application/json");

        // Create a request object with class attributes as parameters
        MyRequestObject requestObject = new MyRequestObject();
        requestObject.setMessageId(messageId);
        requestObject.setEmailStatus(dsn);
        requestObject.setType(type);
        requestObject.setToken(token);

        // Convert the request object to JSON string
        Gson gson = new Gson();
        String jsonPayload = gson.toJson(requestObject);

        // Set the JSON payload as the request entity
        StringEntity requestEntity = new StringEntity(jsonPayload);
        httpPost.setEntity(requestEntity);

        // Send the request and receive the response
        HttpResponse response = httpClient.execute(httpPost);

        // Get the response entity
        HttpEntity entity = response.getEntity();

        // Read the response as a string
        String responseBody = EntityUtils.toString(entity);

        // Process the response as needed
        System.out.println("Print the responseBody:"+responseBody);
    }
   
}




