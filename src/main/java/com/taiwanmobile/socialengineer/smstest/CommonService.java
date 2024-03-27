package com.taiwanmobile.socialengineer.smstest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonService {

	public final static Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);

	public final static Pattern VALID_WHITElIST_REGEX = Pattern
			.compile("^((?!-)[A-Za-z0-9\\-\\*]{1,256}(?<!-)\\.)+[A-Za-z]{2,6}$");

	private static final String PHONE_REGEX = "^09[0-9]{8}$";


	public void sendTwmSms(String phone, String smsbody) {
		Map<String, String> evnMap = new HashMap<>();
		evnMap.put("sSecretKey", "test");
		evnMap.put("sSalt", "test");
		evnMap.put("userName", "VCST011100");
		evnMap.put("password", "9943323456");
		evnMap.put("ratePlan", "A");
		evnMap.put("sendLongUrl","http://bizsms.taiwanmobile.com:18995/send.cgi");
		evnMap.put("sendShortUrl", "http://bizsms.taiwanmobile.com:18994/send.cgi");
		evnMap.put("responseUrl", "");
		evnMap.put("srcaddr", "8706419");
		TwmSms sms = new TwmSms(phone, smsbody, evnMap);
		String[] smsString = sms.switchSms(phone);
		String logTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
		if (smsString[1].startsWith("-"))
			log.info("【{}】【{}】【{}】發送失敗", logTime, phone);
		else
			log.info("【{}】【{}】【{}】發送成功", logTime, phone);

	}

}
