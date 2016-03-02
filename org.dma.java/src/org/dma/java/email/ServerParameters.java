/*******************************************************************************
 * 2008-2016 Public Domain
 * Contributors
 * Marco Lopes (marcolopes@netc.pt)
 *******************************************************************************/
package org.dma.java.email;

import javax.mail.PasswordAuthentication;

import org.dma.java.util.ArrayUtils;

public class ServerParameters {

	public enum SECURITY {

		NONE, STARTTLS, SSLTLS;

		public static final String[] NAMES = ArrayUtils.toStringArray(values());

		public static SECURITY get(int security) {
			try{
				return values()[security];
			}catch(Exception e){}
			return NONE;
		}

		public static SECURITY get(String name) {
			try{
				return valueOf(name);
			}catch(Exception e){}
			return NONE;
		}

	}

	private final String hostName;
	private final int smtpPort;
	private final SECURITY security;
	private final PasswordAuthentication authentication;

	public ServerParameters(String hostName, int smtpPort, int security) {
		this(hostName, smtpPort, security, null);
	}

	public ServerParameters(String hostName, int smtpPort, SECURITY security) {
		this(hostName, smtpPort, security, null);
	}

	public ServerParameters(String hostName, int smtpPort, int security, PasswordAuthentication authentication) {
		this(hostName, smtpPort, SECURITY.get(security), authentication);
	}

	public ServerParameters(String hostName, int smtpPort, SECURITY security, PasswordAuthentication authentication) {
		this.hostName=hostName;
		this.smtpPort=smtpPort;
		this.security=security;
		this.authentication=authentication;
	}


	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Host Name: "+hostName+
				"\nSMTP Port:"+smtpPort+
				"\nSecurity:" +security;
	}


	/*
	 * Getters and setters
	 */
	public String getHostName() {
		return hostName;
	}

	public int getSmtpPort() {
		return smtpPort;
	}

	public SECURITY getSecurity() {
		return security;
	}

	public PasswordAuthentication getAuthentication() {
		return authentication;
	}

}