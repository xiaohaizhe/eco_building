package com.giot.eco_building.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: pyt
 * @Date: 2020/6/29 10:30
 * @Description:
 */
public class StringUtil {
    private static Pattern NUMBER_PATTERN = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");

    /**
     * 利用正则表达式来判断字符串是否为数字
     */
    public static boolean checkStrIsNum(String str) {
        String bigStr;
        try {
            /** 先将str转成BigDecimal，然后在转成String */
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            /** 如果转换数字失败，说明该str并非全部为数字 */
            return false;
        }
        Matcher isNum = NUMBER_PATTERN.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
