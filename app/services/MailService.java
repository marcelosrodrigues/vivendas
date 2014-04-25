package services;

import org.apache.commons.mail.SimpleEmail;

import play.libs.Mail;

public class MailService {

	private final SimpleEmail email = new SimpleEmail();
	
	public MailService from(final String email) {
		try {
			this.email.setFrom(email);			
		} catch (Exception e) {}
		return this;
	}
	
	public MailService to(final String email) {
		try {
			this.email.addTo(email);			
		} catch (Exception e) {}
		return this;
	}
	
	public MailService subject(final String title) {
		try {
			this.email.setSubject(title);
		} catch (Exception e) {}
		return this;
	}
	
	public MailService subject(final String title, String... parameters) {
		return this.subject(String.format(title,parameters));
	}
	
	public MailService message(final String message) {
		try {
			this.email.setSubject(message);			
		} catch (Exception e) {}
		return this;
	}
	
	public MailService message(String message, String... parameters) {
		return this.message(String.format(message,parameters));
	}
	
	public void send() {
		Mail.send(this.email);
	}


	
	
	

}
