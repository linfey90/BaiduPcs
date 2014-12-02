package baidupcs.response.auth;

public class AuthErrorResponse {
	private String error;
	private String error_description;

	/**
	 * @return 错误码
	 */
	public String getError() {
		return error;
	}

	/**
	 * @return 错误描述信息，用来帮助理解和解决发生的错误。
	 */
	public String getError_description() {
		return error_description;
	}

}
