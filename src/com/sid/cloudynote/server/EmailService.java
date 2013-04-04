package com.sid.cloudynote.server;

import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailService {
	public static void sendEmail(String sender, String reciever,
			String subject, String content) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(sender));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
					reciever));
			msg.setSubject(subject);
			
			
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent( content, "text/html; charset=utf-8" ); 

			// use a MimeMultipart as we need to handle the file attachments
			Multipart multipart = new MimeMultipart();

			// add the message body to the mime message
			multipart.addBodyPart( messageBodyPart );

			// Put all message parts in the message
			msg.setContent( multipart ); 
			
//			msg.setText(content);
			Transport.send(msg);

		} catch (AddressException e) {
		} catch (MessagingException e) {
		}
	}
}
