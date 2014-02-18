package com.sheepdog.mail.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.security.auth.RefreshFailedException;
import javax.xml.transform.TransformerException;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sheepdog.business.domain.entities.Subscription;
import com.sheepdog.business.domain.entities.Tweet;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.services.svn.impl.TypeOfFileChanges;
import com.sun.mail.smtp.SMTPTransport;

/**
 * MailConnector singleton provides connection to the mail server. <br />
 * <br />
 * Before sending messages, you need to invoke setup() method to load properties
 * and configuring connection to mail server. <br />
 * Also before invoking sendTweetMessage() and sendSubscriptionMessage() method,
 * you need connect to mail server by openConnection method. After sending
 * message sequence close connection with closeConnection() method.
 * 
 * @author Ivan Arkhipov.
 * 
 */
@Component
@Scope("singleton")
public class MailConnector {

	/**
	 * Host of mail server.
	 */
	@Value("${server.host}")
	private String host;

	/**
	 * Login of app on mail server.
	 */
	@Value("${server.login}")
	private String senderLogin;

	/**
	 * Password of app on mail server.
	 */
	@Value("${server.password}")
	private String senderPassword;

	/**
	 * Path of Velocity HTML template for subscription email.
	 */
	@Value("${template.subscription}")
	private String subscriptionTemplatePath;

	/**
	 * Path of Velocity HTML template for tweet email.
	 */
	@Value("${template.tweet}")
	private String tweetTemplatePath;

	/**
	 * Velocity context to merge templates.
	 */
	private VelocityContext context;

	/**
	 * Velocity object to create templates.
	 */
	private VelocityEngine ve;

	/**
	 * Velocity HTML template for subscription email.
	 */
	private Template subscriptionTemplate;

	/**
	 * Velocity HTML template for tweet email.
	 */
	private Template tweetTemplate;

	/**
	 * Provides sending mail by SMTP protocols.
	 */
	private SMTPTransport mailSender;

	/**
	 * JavaMail session creates SMTPTransport and MimeMessage objects.
	 */
	private Session session;

	/**
	 * Message containing recipient address, mail text and mail content.
	 */
	private MimeMessage message;

	/**
	 * Flag of configuring MailConnector object.
	 */
	private boolean isConfigured = false;

	/**
	 * Logger object.
	 */
	public static final Logger LOG = LoggerFactory.getLogger(MailConnector.class);

	public MailConnector() {

	}

	/**
	 * Send customized message to user.
	 * 
	 * @param template
	 *            Velocity template customizing message.
	 * @param user
	 *            Recipient user.
	 * @throws IOException
	 *             if the message cannot be sent.
	 * @throws TransformerException
	 *             if template merge is failed.
	 */
	private void send(Template template, User user) throws IOException, TransformerException {

		StringWriter w = new StringWriter();

		try {
			template.merge(context, w);
		} catch (ResourceNotFoundException e) {
			LOG.error(e.getMessage() + " on merge templates");
			throw new TransformerException(template.getName());
		} catch (ParseErrorException e) {
			LOG.error(e.getMessage() + " on merge templates");
			throw new TransformerException(template.getName());
		} catch (MethodInvocationException e) {
			LOG.error(e.getMessage() + " on merge templates");
			throw new TransformerException(template.getName());
		} catch (IOException e) {
			LOG.error(e.getMessage() + " on merge templates");
			throw new TransformerException(template.getName());
		}

		message = new MimeMessage(session);

		try {
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));

			message.setSubject("SVN Sheepdog");

			message.setContent(w.toString(), "text/html");

			mailSender.sendMessage(message, message.getAllRecipients());
		} catch (SendFailedException e) {
			LOG.error(e.getLocalizedMessage());
			throw new IOException();
		} catch (MessagingException e) {
			LOG.error(e.getMessage() + " on send message");
			throw new IOException();
		}

	}

	/**
	 * Send message with tweet info. Reload velocity context and send merged
	 * message. Invoke openConnection() method before.
	 * 
	 * @param user
	 *            Recipient user.
	 * @param tweet
	 *            Tweet object containing message.
	 * @throws IOException
	 *             if the message cannot be sent.
	 * @throws TransformerException
	 *             if template merge is failed.
	 * @throws RefreshFailedException
	 *             if update process is failed. See log for more details.
	 */
	public void sendTweetMessage(User user, Tweet tweet) throws IOException, TransformerException,
			RefreshFailedException {
		if (!mailSender.isConnected()) {
			openConnection();
		}

		context.put("user", user);
		context.put("tweet", tweet);
		send(tweetTemplate, user);
	}

	/**
	 * Send message with subscription info. Reload velocity context and send
	 * merged message. Invoke openConnection() method before.
	 * 
	 * @param user
	 *            Recipient user.
	 * @param subscriptions
	 *            Map of Subscription objects.
	 * @throws IOException
	 *             if the message cannot be sent.
	 * @throws TransformerException
	 *             if template merge is failed.
	 * @throws RefreshFailedException
	 *             if update process is failed. See log for more details.
	 */
	public void sendSuscriptionMessage(User user, Map<Subscription, TypeOfFileChanges> subscriptions)
			throws IOException, TransformerException, RefreshFailedException {
		if (!mailSender.isConnected()) {
			openConnection();
		}

		context.put("user", user);
		context.put("subscriptions", subscriptions);
		send(subscriptionTemplate, user);
	}

	/**
	 * Reload updated properties and reconfiguring JavaMail and Velocity
	 * objects.
	 * 
	 * @throws RefreshFailedException
	 *             if update process is failed. See log for more details.
	 */
	public void setup() throws RefreshFailedException {
		isConfigured = false;

		loadProps();

		session = Session.getDefaultInstance(System.getProperties());

		try {
			mailSender = (SMTPTransport) session.getTransport("smtps");
		} catch (MessagingException e) {
			LOG.error(e.getMessage() + " on create SMTPTransport object.");
			throw new RefreshFailedException();
		}

		templateInit();

	}

	/**
	 * Load property from config file.
	 * 
	 * @throws RefreshFailedException
	 *             if file not exist, unavailable or incorrect.
	 */
	private void loadProps() throws RefreshFailedException {
//		Properties prop = new Properties();
//		String propertyPath = "src/main/resources/mail.properties";
//		try (InputStream is = new FileInputStream(new File(propertyPath))) {
//
//			prop.load(is);
//			host = prop.getProperty("server.host");
//			senderLogin = prop.getProperty("server.login");
//			senderPassword = prop.getProperty("server.password");
//			subscriptionTemplatePath = prop.getProperty("template.subscription");
//			tweetTemplatePath = prop.getProperty("template.tweet");
//		} catch (FileNotFoundException e) {
//			LOG.error(e.getMessage() + " " + propertyPath); // TODO
//			throw new RefreshFailedException();
//		} catch (InvalidPropertiesFormatException e) {
//			LOG.error(e.getMessage() + " " + propertyPath); // TODO
//			throw new RefreshFailedException();
//		} catch (IOException e) {
//			LOG.error(e.getMessage() + " " + propertyPath); // TODO
//			throw new RefreshFailedException();
//		}
	}

	/**
	 * Create Velocity object and load templates from files.
	 * 
	 * @throws RefreshFailedException
	 *             if template files incorrect or unavailable.
	 */
	private void templateInit() throws RefreshFailedException {
		try {
			ve = new VelocityEngine();
			ve.init();

			context = new VelocityContext();

			subscriptionTemplate = ve.getTemplate(subscriptionTemplatePath);
			tweetTemplate = ve.getTemplate(tweetTemplatePath);

			isConfigured = true;
		} catch (ResourceNotFoundException e) {
			LOG.error(e.getMessage() + " on load templates");
			throw new RefreshFailedException();
		} catch (ParseErrorException e) {
			LOG.error(e.getMessage() + " on load templates");
			throw new RefreshFailedException();
		} catch (Exception e) {
			LOG.error(e.getMessage() + " on load templates");
			throw new RefreshFailedException();
		}
	}

	/**
	 * Connect to mail server
	 * 
	 * @throws RefreshFailedException
	 *             if update process is failed. See log for more details.
	 * 
	 * @throws IOException
	 *             if connection is failed.
	 */
	public synchronized void openConnection() throws IOException, RefreshFailedException {
		if (!isConfigured) {
			setup();
		}
		try {
			mailSender.connect(host, senderLogin, senderPassword);
		} catch (MessagingException e) {
			LOG.error(e.getLocalizedMessage());
			throw new IOException("Connect to host:" + host + "failed!");
		}

	}

	/**
	 * Close connection to host.
	 */
	public void closeConnection() {
		if (mailSender != null) {
			try {
				mailSender.close();
			} catch (MessagingException e) {
				LOG.error(e.getLocalizedMessage());
			}
		}
	}
}
