import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.Base64.Decoder;
import java.util.List;

import baidupcs.api.BaiduPcs;
import baidupcs.auth.AccessToken;
import baidupcs.auth.BaiduPcsAuth;
import baidupcs.error.api.BaiduPcsException;
import baidupcs.error.api.BaiduPcsFileExistsException;
import baidupcs.error.auth.BaiduPcsAuthException;
import baidupcs.error.auth.InvalidArgsException;
import baidupcs.error.auth.NoRefreshTokenException;
import baidupcs.response.api.FileMetaWithExtra;

public class MainTest {

    public static void main(String args[]) throws ClassNotFoundException {
        String refresh_token = "22.9ed27b0d1ca801650e7a72a167d45b14.315360000.1741414833.2567539370-4425951";
        String api_token = "fBEQIGpLVyygL0symSqSzHNI";
        String secret_token = "09DN9P0vevuu53DTh9GXqCc9lEgUq8UH";
        String appName = "PCS_Pandora2";
        String accessToken = "21.e88a380dbfdd4f7bfc4388eecc1ebb72.2592000.1428646833.2567539370-4425951";
        String expireTime = "1426155150254";

        try {
            //AccessToken ac = BaiduPcsAuth.validateToken(api_token, secret_token, refresh_token, accessToken, appName,
            //        expireTime, retrofit.RestAdapter.LogLevel.FULL);
            AccessToken ac = BaiduPcsAuth.validateToken(api_token, secret_token, refresh_token, accessToken, appName,
                    expireTime, retrofit.RestAdapter.LogLevel.FULL);
           //AccessToken ac = BaiduPcsAuth.validateToken(api_token, secret_token, refresh_token, appName);
            
            //AccessToken ac;
            //ac = BaiduPcsAuth.validateToken(ac);
            
            String go = MainTest.toString(ac);
            System.out.println(go);
            ac = (AccessToken) MainTest.fromString(go);
            
            BaiduPcs pcs = new BaiduPcs(ac);
            
            List<FileMetaWithExtra> tmp = pcs.list("/");
            for(FileMetaWithExtra t : tmp) {
                if(t.isDir()) 
                    System.out.println(t.getFileName());
            }
            
            //Path path = Paths.get("conn.dat");
            //byte[] context = Files.readAllBytes(path);
            /*
            tmp = pcs.list("/0a-00-27-00-00-00/Data/");
            for(FileMetaWithExtra t : tmp) {
                System.out.println(t.getFileName());
            }
            /*
            BufferedReader r = new BufferedReader(new InputStreamReader(pcs.download("/0a-00-27-00-00-00/Data/Schema.xml").in(), StandardCharsets.UTF_8));
            String str = null;
            StringBuilder sb = new StringBuilder();
            while ((str = r.readLine()) != null) {
              sb.append(str + "\n");
            }
            System.out.println("data from InputStream as String : \n" + sb.toString());
            */
        } catch (BaiduPcsFileExistsException e) {
            System.out.println("exist!@!!");
        } catch (BaiduPcsException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println(e.getErrorResponse());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidArgsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BaiduPcsAuthException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getErrorDescription());
            System.out.println(e.getErrorCode());
        } catch (NoRefreshTokenException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /** Read the object from Base64 string. */
    private static Object fromString( String s ) throws IOException ,
                                                        ClassNotFoundException {

        byte[] data = Base64.getDecoder().decode(s);
         ObjectInputStream ois = new ObjectInputStream( 
                                         new ByteArrayInputStream(  data ) );
         Object o  = ois.readObject();
         ois.close();
         return o;
    }

     /** Write the object to a Base64 string. */
     private static String toString( Serializable o ) throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream( baos );
         oos.writeObject( o );
         oos.close();
         return new String(Base64.getEncoder().encode(baos.toByteArray()));
     }

}
