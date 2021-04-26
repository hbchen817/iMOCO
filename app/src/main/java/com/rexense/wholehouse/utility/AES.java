package com.rexense.wholehouse.utility;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: AES
 */
public class AES {
	// AES-CBC-128位零填充加密(不进行Base64编码)
	public static byte[] CBC128ZeroPaddingEncrypt(byte[] src, String key, String iv) {
		if (key == null || iv == null) {
			return null;
		}

		try {
			// 密码处理
			byte[] keyRaw0 = key.getBytes("utf-8");
			byte[] keyRaw = null;
			// Key不为16字节自动补0
			if (key.length() < 16) {
				System.arraycopy(keyRaw0, 0, keyRaw = new byte[16], 0, keyRaw0.length);
			} else {
				System.arraycopy(keyRaw0, 0, keyRaw = new byte[16], 0, 16);
			}
			SecretKeySpec skeySpec = new SecretKeySpec(keyRaw, "AES");

			// 向量处理
			byte[] ivRaw0 = iv.getBytes("utf-8");
			byte[] ivRaw = null;
			// IV不为16字节自动补0
			if (iv.length() < 16) {
				System.arraycopy(ivRaw0, 0, ivRaw = new byte[16], 0, ivRaw0.length);
			} else {
				System.arraycopy(ivRaw0, 0, ivRaw = new byte[16], 0, 16);
			}
			IvParameterSpec ips = new IvParameterSpec(ivRaw);

			// 明文分组零填充
			int length = src.length;
			if(length % 16 != 0) {
				length = length + (16 - (length % 16));
			}
			byte[] bytesPadding = new byte[length];
			System.arraycopy(src, 0, bytesPadding, 0, src.length);

			// 设置算法/模式/补码方式
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ips);
			return cipher.doFinal(bytesPadding);
		} catch (Exception e) {
			Logger.e(String.format("Failed to encrypt the data! The reason may is:\r\n    %s", e.getMessage()));
			return null;
		}
	}

	// AES-CBC-128位零填充解密(不进行Base64编码)
	public static byte[] CBC128ZeroPaddingDecrypt(byte[] src, String key, String iv) {
		if (key == null || iv == null) {
			return null;
		}

		try {
			// 密码处理
			byte[] keyRaw0 = key.getBytes("utf-8");
			byte[] keyRaw = null;
			// Key不为16字节自动补0
			if (key.length() < 16) {
				System.arraycopy(keyRaw0, 0, keyRaw = new byte[16], 0, keyRaw0.length);
			} else {
				System.arraycopy(keyRaw0, 0, keyRaw = new byte[16], 0, 16);
			}
			SecretKeySpec skeySpec = new SecretKeySpec(keyRaw, "AES");

			// 向量处理
			byte[] ivRaw0 = iv.getBytes("utf-8");
			byte[] ivRaw = null;
			// IV不为16字节自动补0
			if (iv.length() < 16) {
				System.arraycopy(ivRaw0, 0, ivRaw = new byte[16], 0, ivRaw0.length);
			} else {
				System.arraycopy(ivRaw0, 0, ivRaw = new byte[16], 0, 16);
			}
			IvParameterSpec ips = new IvParameterSpec(ivRaw);

			// 设置算法/模式/补码方式
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, ips);
			return cipher.doFinal(src);
		} catch (Exception e) {
			Logger.e(String.format("Failed to encrypt the data! The reason may is:\r\n    %s", e.getMessage()));
			return null;
		}
	}

	// AES-CBC-128位零填充加解密测试
	public static void CBC128ZeroPaddingTest(){
		String key = "1111111111111111";
		String iv = "3333333333333333";
		String src = "123456789ABCDEFGHJK";
		byte[] src_byte_encrypt = CBC128ZeroPaddingEncrypt(src.getBytes(), key, iv);
		StringBuilder sb = new StringBuilder();
		for(byte b: src_byte_encrypt){
			sb.append(String.format("%02X ", b));
		}
		byte[] src_decrypt_byte = CBC128ZeroPaddingDecrypt(src_byte_encrypt, key, iv);
		StringBuilder sb1 = new StringBuilder();
		for(byte b: src_decrypt_byte){
			sb1.append(String.format("%02X ", b));
		}
		String src_decrypt = "";
		try {
			src_decrypt = new String(src_decrypt_byte, "utf-8");
		}catch (Exception e){
		}

		Logger.d(String.format("AES-CBC-128 test:\r\n    content: %s\r\n    key: %s\r\n    iv: %s\r\n    content_encrypt_byte: %s\r\n    content_decrypt_byte: %s\n" +
						"    content_decrypt: %s",
				src, key, iv, sb.toString(),sb1.toString(), src_decrypt));
	}
}
