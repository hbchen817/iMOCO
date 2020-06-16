package com.rexense.imoco.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author fanyy
 * @date 2019/12/21
 */
public class TimeUtils {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH");
    public static String startDateTime = "2000-01-01 00:00:00";
    public static String startDate = "2000-01-01";


    public static String getNowTimeString(){
        return sdf1.format(new Date());
    }

    public static long offsetDay(Date date, int offsetUnit){
        long timeStamp1 = date.getTime();
        long timeStamp2 = timeStamp1 + offsetUnit*3600*1000*24l;
        return timeStamp2;
    }

    public static long offsetMonth(Date date, int offsetUnit){
        long timeStamp1 = date.getTime();
        long timeStamp2 = timeStamp1 + offsetUnit*3600*1000*24*30l;
        return timeStamp2;
    }

    public static String getYmd(long timeStamp){
        Date date = new Date(timeStamp);
        return sdf.format(date);
    }

    public static String getYmdhms(Long timeStamp){
        if (timeStamp==null){
            return "";
        }
        Date date = new Date(timeStamp);
        return sdf1.format(date);
    }

    public static String getYmdh(long timeStamp){
        Date date = new Date(timeStamp);
        return sdf2.format(date);
    }

    public static long getTimeStamp(String ymdStr){
        Date date = null;
        try {
            date = sdf.parse(ymdStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static Date getDate(String dateStr){
        try {
            return sdf1.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static String getTodayBeginTime(){
        String ymdStr = sdf.format(new Date());
        return ymdStr+" 00:00:00";
    }
    public static String getTodayEndTime(){
        String ymdStr = sdf.format(new Date());
        return ymdStr+" 23:59:59";
    }

}
