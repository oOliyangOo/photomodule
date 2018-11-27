package com.sina.auto.components.WebCapture;

import com.sina.auto.components.SystemInfo.OSinfo;

/*
 * 判断当前操作系统
 * 
 * 
 */
public class OSname {
    static String osname(){
        String OSname = "";
        OSinfo.EPlatform osname = OSinfo.getOSname();
        System.out.println("操作系统名称=================================="+osname);
        if(String.valueOf(osname).equals("Windows")){
            OSname =  "chromedriver/Windows/chromedriver.exe";
        }else if(String.valueOf(osname).equals("Linux")){
            OSname = "chromedriver/Linux/chromedriver";
        }else if(String.valueOf(osname).equals("Mac OS X")){
            OSname = "chromedriver/Mac/chromedriver";
        }
        return OSname;
    }
}
