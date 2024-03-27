package com.taiwanmobile.socialengineer.smstest;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import lombok.extern.slf4j.Slf4j;

/**
 * TWM簡訊平台API.  目前僅傳送長簡訊。
 * 因此程式為一次性的發送簡訊，  未來若要reuse code, 可再優化程式。
 *
 * 呼叫範例程式:
 * TwmSms sms = new TwmSms("本次要發送的簡訊內容。因只支援長簡訊，故簡訊內容長度必需超過140個字元");
 * sms.sendUCS2LongSms("09XXXXXXXX")
 */
@Slf4j
public class TwmSms {

	private String srcaddr = "";  
	private String userName = "userName";
	private String password = "password";
	private String ratePlan = "ratePlan";
	private boolean isconfigLoaded = false;
	
	private String sendShortUrl = "";
	private String sendLongUrl = "";
	private String responseUrl="";
	
	private String ucs2longbody = "";
	private String ucs2shortbody = "";
	private boolean isLongSMS = false;
	private boolean isShortSMS = false;
	
	/**
	 * 建構函數
	 * @param msbody 長簡訊內容. 若要發
	 */
	public TwmSms(String srcaddr, String smsbody,Map<String, String> evnMap) { 

		checkAndSetSmsBody(smsbody);
		
		 try {
			 //this.srcaddr = srcaddr;
			 userName = evnMap.get("userName");
			 password = evnMap.get("password");
			 ratePlan = evnMap.get("ratePlan");
			 sendLongUrl = evnMap.get("sendLongUrl");
			 sendShortUrl = evnMap.get("sendShortUrl");
			 this.srcaddr = evnMap.get("srcaddr");
			 
			 
			 // 回傳簡訊結果。 
			 String sResponseUrl=evnMap.get("responseUrl");
			 if(sResponseUrl != null && "".equals(sResponseUrl.trim())!= true) {
					 responseUrl= "&response=" +URLEncoder.encode(sResponseUrl,"ISO-8859-1") ;
					 System.out.println(responseUrl);
			 } 
			 
			 this.isconfigLoaded = true;
 
		} catch (IOException e) { 
			log.error(ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		}
	 
	}
	

	private void checkAndSetSmsBody(String smsbody) {
		
		String code = "UTF-16BE"; 
		
		try {
			byte[] bss = smsbody.getBytes(code);
			if(bss.length > 140) {
				ucs2longbody = getSmbodyForLongSms(smsbody, "UTF-16BE", 140);
				isLongSMS = true;
				
			} else if(bss.length > 0) {
				ucs2shortbody = encode(bss);
				isShortSMS = true;
			}
		} catch (UnsupportedEncodingException e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}
	}


	/**
	 * 傳送UCS2 長簡訊 
	 * @param dstaddr 發送手機門號
	 * @return 傳回商務簡訊回傳結果Array, Array內容分別為：msgid, statusstr, point
	 * 	   若walisms.properties未設定好，會回傳-888,  Wrong configuration
	 * 	 若簡訊內容為空值，或是發送門號不正確，則回傳-999, Wrong input
	 */
	public String[] sendUCS2LongSms(String dstaddr) {

		if(isconfigLoaded != true) {
			return new String[]{ dstaddr, "-888", "Wrong configuration" };
		} else if(isLongSMS != true) {
			return new String[]{ dstaddr, "-999", "please use sendUCS2ShortSms() to send short msg." };			
		} else if(isLongSMS != true || dstaddr.length() != 10) {
			return new String[]{ dstaddr, "-999", "Wrong Phone#: " +dstaddr  };
		}
		URL url = null;
		String msgid = "-1";
		String statusstr = "error";
		BufferedReader in = null;
 
		try { 
				String param = "username=" + userName + "&password=" + password
						+ "&rateplan=" + ratePlan + "&srcaddr=" + srcaddr
						+ "&dstaddr=" + dstaddr + "&encoding=LUCS2&smbody="
						+ ucs2longbody+ responseUrl;

				url = new URL(sendLongUrl + "?" + param);
				System.out.println(url.toString());
				HttpURLConnection con = (HttpURLConnection) url
						.openConnection();
				con.setRequestMethod("GET");
				//int responseCode = con.getResponseCode();

				// log("\nSending 'GET' request to URL : " + url);
				// log("Response Code : " + responseCode);

				in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String inputLine;
				Map<String, String> response = new HashMap<String, String>();
				while ((inputLine = in.readLine()) != null) {
					//log(inputLine);
					String[] res = inputLine.split("=");
					if (res.length >= 2) {
						response.put(res[0], res[1]);
					}
				}
				msgid = response.get("msgid");
				statusstr = response.get("statusstr");
				log.debug(dstaddr + "," + msgid + "," + statusstr + "," + new Date().toString());
		} catch (MalformedURLException e) {
			log.error(ExceptionUtils.getStackTrace(e));
			System.out.println("fail to connect to " + sendLongUrl );
			e.printStackTrace();
		} catch (IOException e) {
			log.error(ExceptionUtils.getStackTrace(e));
			System.out.println("fail to connect to " + sendLongUrl );
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
					in = null;
				} catch (Exception e) {
					log.error(ExceptionUtils.getStackTrace(e));
				}
			}
		}
		return new String[]{ dstaddr, msgid, statusstr };
	}

	/**
	 * 依輸入的簡訊字串，產生長簡訊輸入內容。詳細長簡訊encoding規格請參考商務簡訊平台規格書
	 * @param smbody 長簡訊內容
	 * @param code 編碼
	 * @param subMsgLen 每個sub-body的長度
	 * @return 傳回encoding後的字串
	 * @throws UnsupportedEncodingException
	 */
	public String getSmbodyForLongSms(String smbody, String code, int subMsgLen)
			throws UnsupportedEncodingException {

		String encodedBody = "";
		byte[] subbody = null;
		try {

			byte[] header = new byte[6];
			header[0] = (byte) 0x05;
			header[1] = (byte) 0x00;
			header[2] = (byte) 0x03;
			header[3] = (byte) 0xC7;

			byte[] bss = smbody.getBytes(code);
			int subBodyLen = subMsgLen - 6;
			int cnt = bss.length / subBodyLen;
			if (bss.length % subBodyLen > 0) {
				cnt++;
			}
			int totoalLen = bss.length + (cnt * header.length);
			byte[] r = new byte[totoalLen];
			for (int i = 0; i < cnt; i++) {
				header[4] = (byte) cnt;
				header[5] = (byte) (i + 1);
				if ((i + 1) < cnt) {
					subbody = Arrays.copyOfRange(bss, i * subBodyLen, (i + 1)
							* subBodyLen);
				} else {
					subbody = Arrays.copyOfRange(bss, i * subBodyLen,
							bss.length);
				}
				log.debug("E:	" + new String(subbody, code) + ":" + subbody.length);
				System.arraycopy(header, 0, r, i * subMsgLen, header.length);
				System.arraycopy(subbody, 0, r, i * subMsgLen + header.length,
						subbody.length);

			}
			encodedBody = encode(r);

		} catch (UnsupportedEncodingException e) {
			log.error(ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		}
		return encodedBody;
	}

	
	/**
	 * 將byte[]轉成16進位字串
	 * @param b
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String encode(byte[] b) throws UnsupportedEncodingException {

		StringBuilder sb = new StringBuilder();
		for (byte x : b) {
			sb.append(String.format("%%%02X", x));
		}
		return sb.toString();
	}

	
	
	
	/**
	 * 傳送短簡訊
	 * @param dstaddr
	 * @param smbody
	 * @return
	 */
	public String[] sendUCS2ShortSms(String dstaddr) { 
		
		if(isconfigLoaded != true) {
			return new String[]{ dstaddr, "-888", "Wrong configuration" };
		} else if(isShortSMS != true) {
			return new String[]{ dstaddr, "-999", "please use sendUCS2LongSms() to send long msg." };				
		} else if(isShortSMS != true || dstaddr.length() != 10) {
			return new String[]{ dstaddr, "-999", "Wrong Phone#: " +dstaddr  };
		}
		
		URL url = null;
		String msgid = "-1";
		String statusstr = "error";
		BufferedReader in = null;
		try {
			String param = "username=" + userName + "&password=" + password
					+ "&rateplan=" + ratePlan + "&srcaddr=" + srcaddr
					+ "&dstaddr=" + dstaddr + "&encoding=UCS2&smbody="
					+ ucs2shortbody;

			url = new URL(sendShortUrl + "?" + param);
			log.info(sendShortUrl + "?" + param);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			//int responseCode = con.getResponseCode();

			//System.out.println("\nSending 'GET' request to URL : " + url);
			// System.out.println("Response Code : " + responseCode);

			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			Map<String, String> response = new HashMap<String, String>();
			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
				String[] res = inputLine.split("=");
				if (res.length >= 2) {
					response.put(res[0], res[1]);
				}
			}
			msgid = response.get("msgid");
			statusstr = response.get("statusstr");
			log.debug(dstaddr+ ",msgid=" + msgid + ",statusstr=" + statusstr);
		} catch (MalformedURLException e) {
			System.out.println("fail to connect to " + sendShortUrl );
			log.error(ExceptionUtils.getStackTrace(e));
			e.printStackTrace();

		} catch (IOException e) {
			System.out.println("fail to connect to " + sendShortUrl );
			log.error(ExceptionUtils.getStackTrace(e));
			e.printStackTrace();

		} finally {
			if (in != null) {
				try {
					in.close();
					in = null;
				} catch (Exception e) {
					log.error(ExceptionUtils.getStackTrace(e));
				}
			}
		}
		return new String[] {dstaddr, msgid, statusstr };
	}
	
	/*
	 * 傳長簡訊或短簡訊
	 * @param dstaddr
	 */
	public String[] switchSms(String dstaddr) {
		if(isLongSMS) {
			return sendUCS2LongSms(dstaddr);
		}else {
			return sendUCS2ShortSms(dstaddr);
		}
	}
}
