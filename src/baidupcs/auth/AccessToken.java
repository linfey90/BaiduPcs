package baidupcs.auth;

import java.io.Serializable;

/**
 * Access token和应用名。
 * 
 * @author sundaydx
 */
public class AccessToken  implements Serializable {

    private static final long serialVersionUID = 1L;
    private String token;
    private String appName;
    private String apiKey;
    private String secretKey;
    private String refreshToken;
    private String expireTime;

    private void initAccessToken(String token, String appName, String apiKey, String secretKey, String refreshToken,
            String expireTime) {
        this.token = token;
        this.appName = appName;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.refreshToken = refreshToken;
        this.expireTime = expireTime;
    }

    AccessToken(String token, String appName, String apiKey, String secretKey, String refreshToken, String expireTime) {
        initAccessToken(token, appName, apiKey, secretKey, refreshToken, expireTime);
    }

    AccessToken(String token, String appName) {
        initAccessToken(token, appName, null, null, null, null);
    }

    /**
     * @return 返回 ACCESS_TOKEN
     */
    public String getToken() {
        return token;
    }

    /**
     * @return 返回应用名称
     */
    public String getAppName() {
        return appName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String toString() {
	    return "AccessToken: " + this.getToken() + " \nappName: " + this.getAppName() + "\naPPKey: " + this.getApiKey() + "\nSecretKey: " + this.getSecretKey()
	            + "\nrefreshToken: " + this.refreshToken + "\nexpireTime: " + this.expireTime + "\n";
	}
}
