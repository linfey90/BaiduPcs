package baidupcs.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;

import baidupcs.auth.AccessToken;
import baidupcs.error.auth.InvalidArgsException;

public class BaiduUtil {

    public static AccessToken getAccessToken(String url) throws ClassNotFoundException, 
            IOException, InvalidArgsException {
        return GetAccess.getToken(url);
    }

    /** convert InputStream to String */
    public static String getStringFromInputStream(InputStream is) throws IOException {
        return IOUtils.toString(is);
    }

    public static boolean writeInputStream2File(InputStream is, File file) throws IOException {
        OutputStream outputStream = new FileOutputStream(file);

        int read = 0;
        byte[] bytes = new byte[1024];

        while ((read = is.read(bytes)) != -1) {
            outputStream.write(bytes, 0, read);
        }

        outputStream.close();
        return true;
    }

    /** Read the object from Base64 string. */
    public static Object fromString(String s) throws IOException, ClassNotFoundException {

        byte[] data = DatatypeConverter.parseBase64Binary(s);
        // Java 8 特有
        // byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }

    /** Write the object to a Base64 string. */
    public static String toString(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();

        return DatatypeConverter.printBase64Binary(baos.toByteArray());
        // Java 8 特有
        // return new String(Base64.getEncoder().encode(baos.toByteArray()));
    }

}
