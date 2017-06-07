/*******************************************************************************
 * 2008-2017 Public Domain
 * Contributors
 * Marco Lopes (marcolopes@netc.pt)
 *******************************************************************************/
package org.dma.java.email;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailConstants;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.dma.java.email.ServerParameters.SECURITY;
import org.dma.java.util.Debug;

public class CustomHtmlEmail extends HtmlEmail {

	public CustomHtmlEmail(ServerParameters server) {
		Debug.err(server);

		setDebug(Debug.STATUS);
		setCharset(EmailConstants.UTF_8);

		setHostName(server.getHostName());
		setSmtpPort(server.getSmtpPort());

		//Security
		setStartTLSEnabled(server.getSecurity()==SECURITY.STARTTLS);
		setSSLOnConnect(server.getSecurity()==SECURITY.SSLTLS);

		//Authentication
		if (server.getAuthentication()!=null) setAuthentication(
			server.getAuthentication().getUserName(),
			server.getAuthentication().getPassword());
	}


	public Email setFrom(EmailAddress address) throws EmailException {
		if (!address.isValid()) throw new EmailException("Invalid email address (from): "+address);
		return setFrom(address.getEmail(), address.getName());
	}


	public Email addTo(EmailAddress...address) throws EmailException {
		for (EmailAddress element: address){
			if (!element.isValid()) throw new EmailException("Invalid email address (to): "+address);
			addTo(element.getEmail(), element.getName());
		}
		return this;
	}


	public void attach(EmailAttachment...attachment) throws EmailException {
		for (EmailAttachment element: attachment){
			Debug.err(element);
			super.attach(element);
		}
		setBoolHasAttachments(true);
	}


}