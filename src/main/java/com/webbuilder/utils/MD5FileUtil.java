package com.webbuilder.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MD5FileUtil {
	private static final Logger logger = LoggerFactory  
            .getLogger(MD5FileUtil.class);  
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    /**
     * 获取单个文件的MD5值
     * @param path path
     * @return md5
     */
    public static String getFileMd5(File file) {
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;

        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }

        //BigInteger遇到数字0开头的md5会把0去掉，比如MD5: 0BEF4BA1CEB8D1EDE00D339C2BFC8D49
        //BigInteger bigInt = new BigInteger(1, digest.digest());
        //return bigInt.toString(16);
        return toHexString(digest.digest());
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }

}
