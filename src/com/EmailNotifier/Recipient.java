package com.EmailNotifier;

import jakarta.mail.Message.RecipientType;

public class Recipient {

	private final RecipientType type;
	private final String email;

	public Recipient(RecipientType type, String email) {
		this.type = type;
		this.email = email;
	}

	public RecipientType getType() {
		return type;
	}

	public String getEmail() {
		return email;
	}
}