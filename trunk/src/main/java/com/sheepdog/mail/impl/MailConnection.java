package com.sheepdog.mail.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.sheepdog.business.domain.entities.Subscription;
import com.sheepdog.business.domain.entities.Tweet;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.services.svn.impl.TypeOfFileChanges;
import com.sun.mail.smtp.SMTPTransport;

/**
 * MailConnection singleton provides connection to the mail server.
 * 
 * @author Ivan Arkhipov.
 * 
 */
public class MailConnection {

	/**
	 * Host of mail server.
	 */
	private String host;

	/**
	 * Login of app on mail server.
	 */
	private String senderLogin;

	/**
	 * Password of app on mail server.
	 */
	private String senderPassword;

	/**
	 * Path of Velocity HTML template for email.
	 */
	private String templatePath;

	private VelocityContext context;

	private VelocityEngine ve;

	private SMTPTransport mailSender;

	/**
	 * Velocity HTML template for email.
	 */
	private Template template;

	private Session session;

	private MimeMessage message;

	public MailConnection(String host, String senderLogin, String senderPassword, String templatePath) {
		super();
		this.host = host;
		this.senderLogin = senderLogin;
		this.senderPassword = senderPassword;
		this.templatePath = templatePath;

		setup();
	}

	/**
	 * Send message with subscription or tweet info.
	 * 
	 * @param user
	 *            Recipient.
	 * @param necessarySubscriptions
	 *            Map with subscription info. If you need send mail with tweet
	 *            info, set empty HashMap.
	 * @param tweet
	 *            Tweet object. If necessarySubscriptions map is not empty, that
	 *            object ignore in template.
	 */
	public synchronized void send(User user, Map<Subscription, TypeOfFileChanges> necessarySubscriptions, Tweet tweet) {

		context.put("user", user);
		context.put("subscriptions", necessarySubscriptions);
		context.put("tweet", tweet);

		StringWriter w = new StringWriter();

		try {
			template.merge(context, w);
		} catch (ResourceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MethodInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		message = new MimeMessage(session);

		try {
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));

			message.setSubject("SVN Sheepdog");

			message.setContent(w.toString(), "text/html");

			mailSender.sendMessage(message, message.getAllRecipients());
		} catch (SendFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public synchronized void setup() {
		session = Session.getDefaultInstance(System.getProperties());

		try {
			mailSender = (SMTPTransport) session.getTransport("smtps");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		templateInit();

	}

	public synchronized MailConnection openConnection() {
		if (mailSender == null) {
			setup();
		}
		try {
			mailSender.connect(host, senderLogin, senderPassword);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return this;
	}

	public void closeConnection() {
		if (mailSender != null) {
			try {
				mailSender.close();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void templateInit() {
		try {
			ve = new VelocityEngine();
			ve.init();

			context = new VelocityContext();

			template = ve.getTemplate(templatePath);
		} catch (ResourceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
