package com.rm.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @描述：日期工具类
 * @作用：提供日期操作的工具方法
 * @author
 * 
 */
public class DateUtil {
	public interface Pattern {
		public static String CN_FULL_DATE = "yyyy年MM月dd日";
		public static String CN_FULL_DATE_AND_TIME = "yyyy年MM月dd日 HH:mm:ss";
		public static String FULL_DATE_AND_TIME = "yyyy-MM-dd HH:mm:ss";
		public static String FULL_DATE_AND_TIME_NO_LINK = "yyyyMMddHHmmss";
		public static String FULL_DATE = "yyyy-MM-dd";
		public static String FULL_DATE_HOUR = "yyyy-MM-dd HH";
		public static String FULL_DATE_MINUTE = "yyyy-MM-dd HH:mm";

		public static String SHORT_TIME = "HH:mm";
		public static String MIDDLE_TIME = "HH:mm:ss";
		public static String SHORT_TIME_NO_COLON = "HHmm";
		public static String FULL_DATE_AND_TIME_MIC = "yyyy-MM-dd HH:mm:ss:sss";
	}

	public static String getCurrentDateStr_1(String pattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

		return dateFormat.format(new Date());

	}

	/**
	 * 得到日期+i天后的日期
	 * 
	 * @param d
	 * @param i
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static Date addDay(Date d, int i) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(d);
		calendar.add(calendar.DATE, i);// 把日期往后增加一天.整数往后推,负数往前移动
		return calendar.getTime();// 这个时间就是日期往后推一天的结果
	}

	/**
	 * 字符串转日期
	 * 
	 * @param dateStr
	 * @param datePattern
	 * @return
	 */
	public static Date strToDate(String dateStr, String pattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		try {
			return dateFormat.parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException("日期转换异常:日期字符串," + dateStr + ", 格式化串," + pattern);
		}
	}

	/**
	 * 日期转字符串
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String dateToStr(Date date, String pattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(date);
	}

	/**
	 * 时间戳转字符串
	 * 
	 * @param timestamp
	 * @param pattern
	 * @return
	 */
	public static String dateToStr(Timestamp timestamp, String pattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(timestamp);
	}

	/**
	 * 功能描述：获取当前日期。
	 * 
	 * @param pattern
	 * @return
	 */
	public static String getCurrentDateStr(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date());
	}

	/**
	 * 计算两个日期之间相差的天数的方法
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getMinusDays(Date date1, Date date2) {
		Calendar cd1 = new GregorianCalendar();
		Calendar cd2 = new GregorianCalendar();
		cd1.setTime(date1);
		cd2.setTime(date2);
		int days = cd2.get(Calendar.DAY_OF_YEAR) - cd1.get(Calendar.DAY_OF_YEAR);
		int y2 = cd2.get(Calendar.YEAR);
		if (cd1.get(Calendar.YEAR) != y2) { // 如果不是同一年
			do {
				days += cd1.getActualMaximum(Calendar.DAY_OF_YEAR);
				cd1.add(Calendar.YEAR, 1);
			} while (cd1.get(Calendar.YEAR) != y2);
		}
		return days;
	}

	/**
	 * 由指定时间、时间域、数额，计算时间偏移值
	 * 
	 * @param standard
	 *            指定时间
	 * @param type
	 *            时间域
	 * @param amount
	 *            数额
	 * @return 时间值
	 */
	public static Date getDiffDate(Date standard, int type, int amount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(standard);
		cal.add(type, amount);
		return cal.getTime();
	}

	/**
	 * 获取当前时间前指定小时数时间
	 * 
	 * @param hoursNum
	 * @return
	 */
	public static String getPreHoursTimeStr(int hoursNum) {
		String timeStr = "";
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, -hoursNum);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		timeStr = df.format(calendar.getTime());
		return timeStr;
	}
	
	public static Date addByDate(Date srcdate, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(srcdate);
		calendar.add(5, days);
		return calendar.getTime();
	}

	public static Date addByMinute(Date srcdate, int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(srcdate);
		calendar.add(12, minute);
		return calendar.getTime();
	}

	public static Date addByMonth(Date srcdate, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(srcdate);
		calendar.add(2, month);
		return calendar.getTime();
	}

	public static Date addByWeeky(Date srcdate, int weeky) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(srcdate);
		calendar.add(3, weeky);
		return calendar.getTime();
	}

	public static Date addByYaer(Date srcdate, int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(srcdate);
		calendar.add(1, year);
		return calendar.getTime();
	}

}
