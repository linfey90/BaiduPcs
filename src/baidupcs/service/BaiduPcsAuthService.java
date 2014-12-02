package baidupcs.service;

import baidupcs.response.auth.TokenResponse;
import retrofit.http.GET;
import retrofit.http.Query;

public interface BaiduPcsAuthService {
	/**
	 * 构建RestAdapter.Builder需要使用的server配置。
	 */
	String SERVER = "https://openapi.baidu.com/oauth/2.0";

	/**
	 * refreshToken方法的scope参数值，用于请求获取用户在个人云存储中存放的数据的权限。
	 */
	String SCOPE_NETDISK = "netdisk";

	/**
	 * refreshToken方法的grantType参数值。
	 */
	String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

	/**
	 * 使用Refresh Token刷新以获得新的Access Token。
	 * 
	 * @param grantType
	 *            必须参数，固定为“refresh_token”。
	 * @param refreshToken
	 *            必须参数，用于刷新Access Token用的Refresh Token。
	 * @param clientId
	 *            必须参数，应用的API Key。
	 * @param clientSecret
	 *            必须参数，应用的Secret Key。
	 * @param scope
	 *            非必须参数。以空格分隔的权限列表，若不传递此参数，代表请求的数据访问操作权限与上次获取Access
	 *            Token时一致。通过Refresh Token刷新Access
	 *            Token时所要求的scope权限范围必须小于等于上次获取Access Token时授予的权限范围。
	 * @return 响应
	 * @throws Throwable
	 *             ErrorHandler可能返回的任何异常或错误
	 */
	@GET("/token")
	TokenResponse refreshToken(@Query("grant_type") String grantType,
            @Query("refresh_token") String refreshToken,
            @Query("client_id") String clientId,
            @Query("client_secret") String clientSecret) throws Throwable;
}
