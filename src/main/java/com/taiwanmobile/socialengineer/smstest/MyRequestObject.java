package com.taiwanmobile.socialengineer.smstest;

public class MyRequestObject {
    private String messageId;
    private String emailStatus;
    private String type;
    private String token;
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getDsn() {
		return emailStatus;
	}
	public String getType() {
		return type;
	}
	public String getToken() {
		return token;
	}
	public void setEmailStatus(String dsn) {
		this.emailStatus = dsn;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setToken(String token) {
		this.token = token;
	}
	   
	   
}




 

