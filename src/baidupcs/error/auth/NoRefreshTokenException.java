package baidupcs.error.auth;

public class NoRefreshTokenException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoRefreshTokenException(String message) {
		super(message);
	}

}
