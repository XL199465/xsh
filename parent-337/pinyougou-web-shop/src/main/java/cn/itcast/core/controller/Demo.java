package cn.itcast.core.controller;

import java.util.Date;

public class Demo {
    public static void main(String[] args) {
        long time = new Date().getTime();
        long l = System.currentTimeMillis();
        System.currentTimeMillis();
        System.out.println(new Date());
        System.out.println(l);
    }
}
