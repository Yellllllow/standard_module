package com.webbuilder.utils;

import java.security.MessageDigest;

public class EncryptHelper {
	/**
	 * 加密助手实例
	 */
	private static EncryptHelper instance;

	/**
	 * 密码加密前缀
	 */
	public final static String PW_DNEW = "$_";

	/**
	 * 编码格式
	 */
	private static final String encode = "utf-8";

	/**
	 * 获得EncryptHelper对象
	 * 
	 * @return
	 * @throws Exception
	 */
	synchronized static public EncryptHelper getInstance() throws Exception {
		if (instance == null) {
			synchronized (EncryptHelper.class) {
				if (instance == null) {
					instance = new EncryptHelper();
				}
			}
		}
		return instance;
	}
	
	/**
     * 字节数组转化为大写16进制字符串
     * 
     * @param b
     * @return
     */
    private String byte2HexStr(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            String s = Integer.toHexString(b[i] & 0xFF);
            if (s.length() == 1) {
                sb.append("0");
            }
            sb.append(s.toLowerCase());
        }
        return sb.toString();
    }
    private String byteHexStr(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            String s = Integer.toHexString(b[i] & 0xFF);
            if (s.length() == 1) {
                sb.append("0");
            }
            sb.append(s.toLowerCase());
        }
        return sb.toString();
    }
     public String sha1HexStr(String str){
    	 String result = "";
 		try {
 			MessageDigest md = MessageDigest.getInstance("SHA-1");
 			byte[] b = md.digest(str.getBytes(encode));
 			result = byte2HexStr(b);
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
 		return result;
     }
     public byte[] sha1(String str){
 		try {
 			MessageDigest md = MessageDigest.getInstance("SHA-1");
 			byte[] b = md.digest(str.getBytes(encode));
 			return b;
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
 		return null;
     }
	/**
	 * 加密
	 * 
	 * @param str
	 * @return
	 */
	public String encrypt(String str) {
		String result = "";
		try {
			//原来的加密代码
			//MessageDigest md = MessageDigest.getInstance("SHA-1");
			//byte[] b = md.digest(str.getBytes(encode));
			//result = PW_DNEW + byte2HexStr(b);
			return MD5(str);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
    public String MD5(String str){
    	String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] b = md.digest(str.getBytes(encode));
			result = byte2HexStr(b);
			result = result.substring(8, 24);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
    }
	/**
	 * 测试主函数
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String str = "123456";
			System.out.println(EncryptHelper.getInstance().encrypt(str));
			System.out.println(EncryptHelper.getInstance().encrypt(str));
			System.out.println(EncryptHelper.getInstance().encrypt(str)
					.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getEncode() {
		return encode;
	}
}
