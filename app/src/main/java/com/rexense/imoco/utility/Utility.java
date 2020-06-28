package com.rexense.imoco.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 15:29
 * Description: 公用库
 */
public class Utility {
	// 休眠指定秒数
	public static void sleepSecond(int second) {
		try {
			Thread.sleep(second * 1000);
		} catch (Exception e) {}
	}

	// 休眠指定毫秒数
	public static void sleepMilliSecond(int milliSecond) {
		try {
			Thread.sleep(milliSecond);
		} catch (Exception e) {}
	}

	// 获取当前时间戳(毫秒)
	public static long getCurrentTimeStamp() {
		return System.currentTimeMillis();
	}

	// 时间戳(毫秒)转换长字符串(yyyy-MM-dd HH:mm:ss)
	public static String timeStampToLongString(long timeStamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(timeStamp);
	}

	// 时间戳(毫秒)转换年月日字符串(yyyy-MM-dd)
	public static String timeStampToYMDString(long timeStamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(timeStamp);
	}

	// 时间戳(毫秒)转换时分秒字符串(HH:mm:ss)
	public static String timeStampToHMSString(long timeStamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(timeStamp);
	}

	// 时间戳(毫秒)转换时分字符串(HH:mm)
	public static String timeStampToHMString(long timeStamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(timeStamp);
	}

	// 格式字符串(yyyy-MM-dd HH:mm:ss)转换成时间戳(毫秒)
	public static long formatStringToTimeStamp(String dateTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long timeStamp = 0;
		try {
			Date time= sdf.parse(dateTime);
			timeStamp = time.getTime();
		} catch (Exception ex) {
			timeStamp = 0;
		}
		return timeStamp;
	}
}
