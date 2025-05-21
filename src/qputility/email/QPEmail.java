package qputility.email;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class QPEmail
{

	/**
	 * @param args
	 * @throws MessagingException 
	 */
	public static void main(String[] args) throws MessagingException
	{
		System.out.println("Starting");

	}

	public static void sendMail(String from, List<String> to, String subject, String message) throws MessagingException
	{
		Address[] targets = new Address[to.size()];
		for (int i = 0; i < to.size(); i++)
		{
			targets[i] = new InternetAddress(to.get(i));
		}
		
//		Address[] targets = new Address[to.length];
//		for (int i = 0; i < to.length; i++)
//		{
//			targets[i] = new InternetAddress(to[i]);
//		}
		String host = "apps.smtp.gov.bc.ca";
		Properties properties = System.getProperties();

		properties.setProperty("mail.smtp.host", host);

		Session session = Session.getDefaultInstance(properties);
		
		MimeMessage _message = new MimeMessage(session);

		_message.setFrom(new InternetAddress(from));

		_message.addRecipients(Message.RecipientType.TO, targets);

		_message.setSubject(subject);

		_message.setContent(message, "text/html");

		Transport.send(_message);

	}
	
	public static void sendMail(String from, List<String> to, String subject, String message, String host) throws MessagingException
	{
		Address[] targets = new Address[to.size()];
		for (int i = 0; i < to.size(); i++)
		{
			targets[i] = new InternetAddress(to.get(i));
		}
		
//		Address[] targets = new Address[to.length];
//		for (int i = 0; i < to.length; i++)
//		{
//			targets[i] = new InternetAddress(to[i]);
//		}
		
		Properties properties = System.getProperties();

		properties.setProperty("mail.smtp.host", host);

		Session session = Session.getDefaultInstance(properties);
		
		MimeMessage _message = new MimeMessage(session);

		_message.setFrom(new InternetAddress(from));

		_message.addRecipients(Message.RecipientType.TO, targets);

		_message.setSubject(subject);

		_message.setContent(message, "text/html");

		Transport.send(_message);

	}
}
