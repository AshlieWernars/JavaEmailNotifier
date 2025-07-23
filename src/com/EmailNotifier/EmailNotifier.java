package com.EmailNotifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailNotifier {

	public static void writeEmail(String fromEmail, String fromEmailPassword, ArrayList<Recipient> recipients, String subject, String emailContents) throws IllegalArgumentException {
		if (fromEmail == null || fromEmail.trim().isEmpty()) {
			throw new IllegalArgumentException("ERROR: fromEmail is null or empty");
		}

		if (fromEmailPassword == null || fromEmailPassword.trim().isEmpty()) {
			throw new IllegalArgumentException("ERROR: fromEmailPassword is null or empty");
		}

		if (recipients == null || recipients.isEmpty()) {
			throw new IllegalArgumentException("ERROR: recipients is null or empty");
		}

		if (subject == null) {
			throw new IllegalArgumentException("ERROR: subject is null");
		}

		if (emailContents == null) {
			throw new IllegalArgumentException("ERROR: emailContents is null");
		}

		// Set up mail server properties
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		// Create a session with authentication
		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, fromEmailPassword);
			}
		});

		Map<Message.RecipientType, List<String>> groupedRecipients = groupRecipientsByType(recipients);

		try {
			// Construct the message
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromEmail));

			// Set recipients for each type at once
			for (Map.Entry<Message.RecipientType, List<String>> entry : groupedRecipients.entrySet()) {
				String emails = String.join(",", entry.getValue());
				message.setRecipients(entry.getKey(), InternetAddress.parse(emails));
			}

			message.setSubject(subject);
			message.setText(emailContents);

			// Send the message
			Transport.send(message);
			System.out.println("Email sent successfully.");

		} catch (MessagingException e) {
			System.err.println("[!] Email send failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static Map<RecipientType, List<String>> groupRecipientsByType(ArrayList<Recipient> recipients) {
		Map<Message.RecipientType, List<String>> groupedRecipients = new HashMap<>();
		for (Recipient recipient : recipients) {
			groupedRecipients.computeIfAbsent(recipient.getType(), k -> new ArrayList<>()).add(recipient.getEmail());
		}
		return groupedRecipients;
	}
}