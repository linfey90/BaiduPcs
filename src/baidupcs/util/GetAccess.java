package baidupcs.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import baidupcs.auth.AccessToken;
import baidupcs.error.auth.InvalidArgsException;

public class GetAccess {

    /**
     * 从指定url获取AccessToken
     * 
     * @param url
     * @return
     * @throws IOException
     * @throws InvalidArgsException
     * @throws ClassNotFoundException
     */
    public static AccessToken getToken(String url) throws IOException, 
        InvalidArgsException, ClassNotFoundException {
        
        String html = getHtmlContent(url).replace(" ", "+");
        String AccessToken = findToken(html);
        
        return (AccessToken) BaiduUtil.fromString(AccessToken);
    }

    /**
     * 从网页中利用正则获取AccessToken
     * 
     * @param html
     *            网页内容
     * @return AccessToken
     * @throws InvalidArgsException
     */
    private static String findToken(String html) throws InvalidArgsException {
        String REGEX = "base64,[\\w,\\+,\\=]*";
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(html);

        while (m.find()) {
            String ac = html.substring(m.start() + 7, m.end());
            while(ac.length()%4 != 0) {
                ac = ac + "=";
            }
            return ac;
        }

        throw new InvalidArgsException("Error URL");
    }

    /**
     * 获取网页内容
     * 
     * @param htmlurl
     *            网址
     * @return 页面内容
     * @throws IOException
     * @throws InvalidArgsException
     */
    private static String getHtmlContent(String htmlurl) throws IOException, InvalidArgsException {
        URL url;
        String temp;
        StringBuffer sb = new StringBuffer();
        try {
            url = new URL(htmlurl);
            
            // 读取网页全部内容
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream(), "utf-8"));
            while ((temp = in.readLine()) != null) {
                sb.append(temp);
            }
            in.close();
        } catch (MalformedURLException me) {
            throw new InvalidArgsException("Error URL");
        } catch (IOException e) {
            throw e;
        }
        return sb.toString();
    }
    
    public static boolean setNewAc(String htmlurl, AccessToken ac) throws IOException, InvalidArgsException {
        String accessToken = BaiduUtil.toString(ac);
        htmlurl = htmlurl + accessToken;
        
        String html = getHtmlContent(htmlurl).replace(" ", "+");
        
        return html.equals(accessToken);
    }

}
