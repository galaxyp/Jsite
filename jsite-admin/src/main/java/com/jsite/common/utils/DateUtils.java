package com.jsite.common.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author jsite
 */
public class DateUtils {

    public static final String YYYY = "yyyy";
    public static final String YYYY_MM = "yyyy-MM";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期字符串
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    /**
     * 获取当前时间字符串
     */
    public static String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 获取当前日期时间字符串
     */
    public static String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    /**
     * 获取当前日期时间字符串
     */
    public static String dateTimeNow(String format) {
        return parseDateToStr(format, new Date());
    }

    /**
     * 日期格式化
     */
    public static String parseDateToStr(String format, Date date) {
        return DateUtil.format(date, format);
    }

    /**
     * 字符串转日期
     */
    public static Date parseDate(String dateStr) {
        return DateUtil.parse(dateStr);
    }

    /**
     * 字符串转日期
     */
    public static Date parseDate(String dateStr, String format) {
        return DateUtil.parse(dateStr, format);
    }

    /**
     * 获取当前LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前LocalDate
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * Date转LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTimeUtil.of(date);
    }

    /**
     * LocalDateTime转Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime格式化
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * LocalDateTime格式化为默认格式
     */
    public static String format(LocalDateTime dateTime) {
        return format(dateTime, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 字符串解析为LocalDateTime
     */
    public static LocalDateTime parseLocalDateTime(String dateStr, String pattern) {
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 字符串解析为LocalDateTime（默认格式）
     */
    public static LocalDateTime parseLocalDateTime(String dateStr) {
        return parseLocalDateTime(dateStr, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 获取一天的开始时间
     */
    public static LocalDateTime beginOfDay(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MIN);
    }

    /**
     * 获取一天的结束时间
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MAX);
    }

    /**
     * 计算两个日期之间的天数
     */
    public static long betweenDays(LocalDate start, LocalDate end) {
        return LocalDateTimeUtil.between(start, end).toDays();
    }

    /**
     * 计算两个时间之间的秒数
     */
    public static long betweenSeconds(LocalDateTime start, LocalDateTime end) {
        return LocalDateTimeUtil.between(start, end).getSeconds();
    }

    /**
     * 判断是否为同一天
     */
    public static boolean isSameDay(LocalDate date1, LocalDate date2) {
        return date1.equals(date2);
    }

    /**
     * 日期加天数
     */
    public static LocalDateTime plusDays(LocalDateTime dateTime, long days) {
        return dateTime.plusDays(days);
    }

    /**
     * 日期减天数
     */
    public static LocalDateTime minusDays(LocalDateTime dateTime, long days) {
        return dateTime.minusDays(days);
    }
}
