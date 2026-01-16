package com.jsite.common.utils;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Collection;
import java.util.Map;

/**
 * 字符串工具类
 *
 * @author jsite
 */
public class StringUtils extends CharSequenceUtil {

    /**
     * 空字符串
     */
    private static final String NULLSTR = "";

    /**
     * 下划线
     */
    private static final char SEPARATOR = '_';

    /**
     * 判断是否为空
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        } else if (obj instanceof Collection<?>) {
            return ((Collection<?>) obj).isEmpty();
        } else if (obj instanceof Map<?, ?>) {
            return ((Map<?, ?>) obj).isEmpty();
        } else if (obj.getClass().isArray()) {
            return java.lang.reflect.Array.getLength(obj) == 0;
        }
        return false;
    }

    /**
     * 判断是否不为空
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean isNull(Object object) {
        return object == null;
    }

    /**
     * 判断字符串是否不为空
     */
    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    /**
     * 去除字符串首尾空格
     */
    public static String trim(String str) {
        return str == null ? NULLSTR : str.trim();
    }

    /**
     * 截取字符串
     */
    public static String substring(String str, int start) {
        if (str == null) {
            return NULLSTR;
        }
        if (start < 0) {
            start = str.length() + start;
        }
        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return NULLSTR;
        }
        return str.substring(start);
    }

    /**
     * 截取字符串
     */
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return NULLSTR;
        }
        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }
        if (end > str.length()) {
            end = str.length();
        }
        if (start > end) {
            return NULLSTR;
        }
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }
        return str.substring(start, end);
    }

    /**
     * 格式化字符串
     */
    public static String format(String template, Object... params) {
        return StrUtil.format(template, params);
    }

    /**
     * 是否包含字符串
     */
    public static boolean inStringIgnoreCase(String str, String... strs) {
        if (str != null && strs != null) {
            for (String s : strs) {
                if (str.equalsIgnoreCase(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 驼峰转下划线
     */
    public static String toUnderScoreCase(String str) {
        return StrUtil.toUnderlineCase(str);
    }

    /**
     * 下划线转驼峰
     */
    public static String toCamelCase(String str) {
        return StrUtil.toCamelCase(str);
    }

    /**
     * 首字母大写
     */
    public static String capitalize(String str) {
        return StrUtil.upperFirst(str);
    }

    /**
     * 首字母小写
     */
    public static String uncapitalize(String str) {
        return StrUtil.lowerFirst(str);
    }

    /**
     * 检查字符串是否以某个字符串结尾（忽略大小写）
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        return StrUtil.endWithIgnoreCase(str, suffix);
    }

    /**
     * 检查字符串是否以某个字符串开头（忽略大小写）
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        return StrUtil.startWithIgnoreCase(str, prefix);
    }

    /**
     * 将字符串转为int
     */
    public static int toInt(Object val, int defaultValue) {
        if (val == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(trim(val.toString()));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 将字符串转为long
     */
    public static long toLong(Object val, long defaultValue) {
        if (val == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(trim(val.toString()));
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
