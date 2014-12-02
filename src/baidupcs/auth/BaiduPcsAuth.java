package baidupcs.auth;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import retrofit.RestAdapter.LogLevel;
import baidupcs.api.BaiduPcs;
import baidupcs.error.api.BaiduPcsException;
import baidupcs.error.auth.BaiduPcsAuthException;
import baidupcs.error.auth.InvalidArgsException;
import baidupcs.error.auth.NoRefreshTokenException;

public class BaiduPcsAuth {

    private static final String PROP_KEY_API_KEY = "api_key";
    private static final String PROP_KEY_SECRET_KEY = "secret_key";
    private static final String PROP_KEY_REFRESH_TOKEN = "refresh_token";
    private static final String PROP_KEY_ACCESS_TOKEN = "access_token";
    private static final String PROP_KEY_EXPIRE_TIME = "expire_time";
    private static final String PROP_KEY_APP_NAME = "app_name";

    /**
     * token验证 (配置文件验证)
     * 
     * @param propsFilePath
     *            配置文件地址
     * @param charset
     *            配置文件字符集
     * @return AccessToken accestoken等数据
     * @throws IOException
     *             配置文件打开失败
     * @throws InvalidArgsException
     *             必须的配置信息空缺
     * @throws BaiduPcsAuthException
     *             百度云验证失败
     * @throws NoRefreshTokenException
     *             缺少refresh_token
     */
    public static AccessToken validateToken(Path propsFilePath, Charset charset) throws IOException,
            InvalidArgsException, BaiduPcsAuthException, NoRefreshTokenException, BaiduPcsException {
        Properties props = new Properties();
        try (Reader propsReader = Files.newBufferedReader(propsFilePath, charset)) {
            props.load(propsReader);
        }
        String appName = props.getProperty(PROP_KEY_APP_NAME);
        String apiKey = props.getProperty(PROP_KEY_API_KEY);
        String secretKey = props.getProperty(PROP_KEY_SECRET_KEY);
        String refreshToken = props.getProperty(PROP_KEY_REFRESH_TOKEN);
        String accessToken = props.getProperty(PROP_KEY_ACCESS_TOKEN);
        String expireTime = props.getProperty(PROP_KEY_EXPIRE_TIME);

        AccessToken ac = validateToken(apiKey, secretKey, refreshToken, accessToken, appName, expireTime, null);

        props.setProperty(PROP_KEY_ACCESS_TOKEN, ac.getToken());
        props.setProperty(PROP_KEY_EXPIRE_TIME, ac.getExpireTime());
        props.setProperty(PROP_KEY_REFRESH_TOKEN, ac.getRefreshToken());

        try (Writer propsWriter = Files.newBufferedWriter(propsFilePath, charset)) {
            props.store(propsWriter, "Baidupcs authorization infomation.");
        }

        return ac;
    }

    /**
     * token验证 (AccessToken验证)
     * 
     * @param ac
     *            AccessToken
     * @return AccessToken accestoken等数据
     * @throws IOException
     *             配置文件打开失败
     * @throws InvalidArgsException
     *             必须的配置信息空缺
     * @throws BaiduPcsAuthException
     *             百度云验证失败
     * @throws NoRefreshTokenException
     *             缺少refresh_token*
     * @throws BaiduPcsException
     *             百度云连接测试失败
     */
    public static AccessToken validateToken(AccessToken ac) throws BaiduPcsException, IOException,
            InvalidArgsException, BaiduPcsAuthException, NoRefreshTokenException {
        return validateToken(ac.getApiKey(), ac.getSecretKey(), ac.getRefreshToken(), ac.getToken(), ac.getAppName(),
                ac.getExpireTime(), null);
    }

    /**
     * token验证 (配置文件验证)
     * 
     * @param propsFilePath
     *            配置文件地址
     * @return AccessToken accestoken等数据
     * @throws IOException
     *             配置文件打开失败
     * @throws InvalidArgsException
     *             必须的配置信息空缺
     * @throws BaiduPcsAuthException
     *             百度云验证失败
     * @throws NoRefreshTokenException
     *             缺少refresh_token
     */
    public static AccessToken validateToken(Path propsFilePath) throws IOException, InvalidArgsException,
            BaiduPcsAuthException, NoRefreshTokenException, BaiduPcsException {
        return validateToken(propsFilePath, Charset.defaultCharset());
    }

    /**
     * token验证(字符串)
     * 
     * @param apiKey
     *            apiKey
     * @param secretKey
     *            secretKey
     * @param refreshToken
     *            refreshToken
     * @param appName
     *            应用名称
     * @return AccessToken accestoken等数据
     * @throws IOException
     *             配置文件打开失败
     * @throws InvalidArgsException
     *             必须的配置信息空缺
     * @throws BaiduPcsAuthException
     *             百度云验证失败
     * @throws NoRefreshTokenException
     *             缺少refresh_token*
     * @throws BaiduPcsException
     *             百度云连接测试失败
     */
    public static AccessToken validateToken(String apiKey, String secretKey, String refreshToken, String appName)
            throws IOException, InvalidArgsException, BaiduPcsAuthException, NoRefreshTokenException, BaiduPcsException {

        return validateToken(apiKey, secretKey, refreshToken, null, appName, null, null);
    }

    /**
     * token验证(字符串)
     * 
     * @param accessToken
     *            accessToken
     * @param appName
     *            应用名称
     * @param expireTime
     *            过期时间
     * @return AccessToken accestoken等数据
     * @throws IOException
     *             配置文件打开失败
     * @throws InvalidArgsException
     *             必须的配置信息空缺
     * @throws BaiduPcsAuthException
     *             百度云验证失败
     * @throws NoRefreshTokenException
     *             缺少refresh_token*
     * @throws BaiduPcsException
     *             百度云连接测试失败
     */
    public static AccessToken validateToken(String accessToken, String appName, String expireTime) throws IOException,
            InvalidArgsException, BaiduPcsAuthException, NoRefreshTokenException, BaiduPcsException {
        return validateToken(null, null, null, accessToken, appName, expireTime, null);
    }

    /**
     * token验证(字符串)
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
     *            应用名称
     * @param expireTime
     *            过期时间
     * @return AccessToken accestoken等数据
     * @throws IOException
     *             配置文件打开失败
     * @throws InvalidArgsException
     *             必须的配置信息空缺
     * @throws BaiduPcsAuthException
     *             百度云验证失败
     * @throws NoRefreshTokenException
     *             缺少refresh_token*
     * @throws BaiduPcsException
     *             百度云连接测试失败
     */
    public static AccessToken validateToken(String apiKey, String secretKey, String refreshToken, String accessToken,
            String appName, String expireTime, LogLevel level) throws IOException, InvalidArgsException,
            BaiduPcsAuthException, NoRefreshTokenException, BaiduPcsException {
        AccessToken ac = BaiduPcsAuthCheck.validateTokenCheck(apiKey, secretKey, refreshToken, accessToken, appName,
                expireTime, level);
        BaiduPcs pcs = new BaiduPcs(ac.getToken(), ac.getAppName(), level);
        pcs.list("/");

        return ac;
    }

    public BaiduPcsAuth() {
        // TODO Auto-generated constructor stub
    }

}
