package com.brahvim.agc.server;

public enum ExitCode {

	OKAY("User exited app."),
	UNKNOWN("Unknown <:(..."),

	SSL_SOCKET_CREATION_PERMISSION("SSL socket creation not allowed."),
	SSL_SOCKET_ACCEPT_PERMISSION("SSL socket not allowed to accept connections."),

	WELCOME_SOCKET_TIMEOUT("Welcome socket (SSL) timed out."),
	WELCOME_SOCKET_PORT_UNAVAILABLE("Welcome socket (SSL) could not be started on assigned port."),

	/*	*/ ;

	public static final String ERROR_MESSAGE_PREFIX = "Exiting! Reason: ";

	public final String errorMessage;

	private ExitCode(final String p_errorMessage) {
		this.errorMessage = p_errorMessage;
	}

}
