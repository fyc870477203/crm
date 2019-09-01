package com.bjpowernode.test;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 作者:fyc
 * 2019/8/30
 */
public class Test {
    public static void main(String[] args) {
       LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time=dateTime.format(dtf);
        System.out.println(time);
        test();
    }

    private static void test() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String time = sdf.format(date);
        System.out.println(time);
    }
}
