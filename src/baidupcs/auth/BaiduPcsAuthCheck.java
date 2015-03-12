package baidupcs.auth;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import baidupcs.error.auth.AuthErrorParseHandler;
import baidupcs.error.auth.BaiduPcsAuthException;
import baidupcs.error.auth.InvalidArgsException;
import baidupcs.error.auth.NoRefreshTokenException;
import baidupcs.response.auth.TokenResponse;
import baidupcs.service.BaiduPcsAuthService;

/**
 * Origin https://github.com/blovemaple/java-baidupcs
 * 
 * @author sundaydx
 * 
 *         更改代码，简化，符合项目需求
 *         <p>
 *         提供 ACCESS_TOKEN 的验证，以及根据 REFRESH_TOKEN 获取 ACCESS_TOKEN
 * 
 * @create 2014.11.23
 * @latest 2014.11.23
 */
public class BaiduPcsAuthCheck {

	private final String apiKey;
	private final String secretKey;
	private BaiduPcsAuthService service;

	private static final String PROP_KEY_API_KEY = "api_key";
	private static final String PROP_KEY_SECRET_KEY = "secret_key";
	private static final String PROP_KEY_EXPIRE_TIME = "expire_time";
	private static final String PROP_KEY_APP_NAME = "app_name";

	/**
	 * 创建一个实例。
	 * 
	 * @param apiKey
	 *            应用的API Key
	 * @param secretKey
	 *            应用的Secret Key
	 */
	public BaiduPcsAuthCheck(String apiKey, String secretKey, LogLevel level) {
		this.apiKey = apiKey;
		this.secretKey = secretKey;

		ErrorHandler errorHandler = new AuthErrorParseHandler();
		service = new RestAdapter.Builder().setLogLevel(level)
				.setEndpoint(BaiduPcsAuthService.SERVER)
				.setErrorHandler(errorHandler).build()
				.create(BaiduPcsAuthService.class);
	}

	/**
	 * 使用Refresh Token刷新以获得新的Access Token。
	 * 
	 * @param refreshToken
	 *            必须参数，用于刷新Access Token用的Refresh Token。
	 * @return 响应
	 * @throws BaiduPcsAuthException
	 */
	public TokenResponse refreshToken(String refreshToken)
			throws BaiduPcsAuthException {
		try {
			/*return service.refreshToken(
					BaiduPcsAuthService.GRANT_TYPE_REFRESH_TOKEN, refreshToken,
					apiKey, secretKey, BaiduPcsAuthService.SCOPE_NETDISK);*/
		    return service.refreshToken(
                    BaiduPcsAuthService.GRANT_TYPE_REFRESH_TOKEN, refreshToken,
                    apiKey, secretKey);
		} catch (BaiduPcsAuthException e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 检测 ACCESS_TOKEN 是否可用,获取更新之
	 * 
	 * @param apiKey
	 *            apiKey
	 * @param secretKey
	 *            secretKey
	 * @param refreshToken
	 *            refreshToken
	 * @param accessToken
	 *            accessToken
	 * @param appName
	 *            appName
	 * @param expireTime
	 *            expireTime
	 * @return AccessToken对象,包涵最新的access_token等
	 * @throws InvalidArgsException
	 *             必须的配置参数为空
	 * @throws BaiduPcsAuthException
	 *             云盘授权失败
	 * @throws NoRefreshTokenException
	 *             缺少refresh_token
	 */
	public static AccessToken validateTokenCheck(String apiKey,
			String secretKey, String refreshToken, String accessToken,
			String appName, String expireTime, LogLevel level) throws InvalidArgsException,
			BaiduPcsAuthException, NoRefreshTokenException {

		Date expireTimeDate = null;
		if (expireTime != null && !expireTime.isEmpty()) {
			try {
				expireTimeDate = new Date(Long.parseLong(expireTime) - 86400);
			} catch (NumberFormatException e) {
				throw new InvalidArgsException(PROP_KEY_EXPIRE_TIME
						+ " is not a valid time.");
			}
		}

		checkPropEmpty(PROP_KEY_APP_NAME, appName);

		Date now = new Date();

		/** 依据有效时间判断 ACCESS_TOKEN 是否需要再次获取 */
		if (accessToken != null && !accessToken.isEmpty()
				&& expireTimeDate != null && now.before(expireTimeDate))
			return new AccessToken(accessToken, appName, apiKey, secretKey, refreshToken,
		            expireTime);

		checkPropEmpty(PROP_KEY_API_KEY, apiKey);
		checkPropEmpty(PROP_KEY_SECRET_KEY, secretKey);

		BaiduPcsAuthCheck auth = new BaiduPcsAuthCheck(apiKey, secretKey, level);
		TokenResponse token;
		if (refreshToken != null && !refreshToken.isEmpty()) {
			token = auth.refreshToken(refreshToken);
		} else {
			throw new NoRefreshTokenException("Not have a valid refresh_token");
		}

		String newAccessToken = token.getAccess_token();
		long newExpireTime = now.getTime()
				+ TimeUnit.MILLISECONDS.convert(token.getExpires_in(),
						TimeUnit.SECONDS);
		String newRefreshToken = token.getRefresh_token();

		return new AccessToken(newAccessToken, appName, apiKey, secretKey,
				newRefreshToken, Long.toString(newExpireTime));

	}


	private static void checkPropEmpty(String key, String value)
			throws InvalidArgsException {
		if (value == null || value.isEmpty())
			throw new InvalidArgsException(key + " should not be empty.");
	}
}
