package com.qqkj.inspection.common.utils;

public class MD5Util {

	protected MD5Util(){

	}

	private static final String ALGORITH_NAME = "md5";

	private static final int HASH_ITERATIONS = 2;

	public static String encrypt(String password) {
       return MD5Tool.getMD5String(password.toLowerCase());
//		return new SimpleHash(ALGORITH_NAME, password, ByteSource.Util.bytes(password), HASH_ITERATIONS).toHex();
	}

	public static String encrypt(String username, String password) {
        return MD5Tool.getMD5String(password.toLowerCase());
//		return new SimpleHash(ALGORITH_NAME, password, ByteSource.Util.bytes(username.toLowerCase() + password),
//				HASH_ITERATIONS).toHex();
	}

	public static void main(String[] args) {
		System.out.println(encrypt("123456"));
	}

}
