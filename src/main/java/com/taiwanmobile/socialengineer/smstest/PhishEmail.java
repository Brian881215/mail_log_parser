package com.taiwanmobile.socialengineer.smstest;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Data
public class PhishEmail {
	
	@Id
	@GeneratedValue
	@Column(name = "phish_email_id")
	Integer phishEmailId;
	Integer campaignId;
	Integer phishEmailsSettingMapId;
	Integer phishUser;
	String token;
	Date createdTime;
	Date scheduledTime;
	Date sendTime;
	String emailStatus;
	String emailResponseCode;
	String emailResponseMessage;
	String emailContent;
	String messageId;
	String postfixQueueId;
	String postfixDsnCode;
	String postfixStatus;



	
}