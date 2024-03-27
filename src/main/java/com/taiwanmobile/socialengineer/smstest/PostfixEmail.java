package com.taiwanmobile.socialengineer.smstest;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;



@Entity
@Data
public class PostfixEmail {
	@Id
	@GeneratedValue
	@Column(name = "postfix_email_id")
	Integer postfixEmailId;
	String messageId;
	String queueId;
	String dsn;

}





