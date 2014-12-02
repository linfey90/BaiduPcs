package baidupcs.response.auth;

public class TokenResponse {
	private String access_token;
	private int expires_in;
	private String refresh_token;
	private String scope;
	private String session_key;
	private String session_secret;

	/**
	 * @return Access Token
	 */
	public String getAccess_token() {
		return access_token;
	}

	/**
	 * @return Access Token的有效期，以秒为单位
	 */
	public int getExpires_in() {
		return expires_in;
	}

	/**
	 * @return 用于刷新Access Token的Refresh Token
	 */
	public String getRefresh_token() {
		return refresh_token;
	}

	/**
	 * @return Access Token最终的访问范围
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * @return 基于http调用Open API时所需要的Session Key，其有效期与Access Token一致
	 */
	public String getSession_key() {
		return session_key;
	}

	/**
	 * @return 基于http调用Open API时计算参数签名用的签名密钥
	 */
	public String getSession_secret() {
		return session_secret;
	}

}
