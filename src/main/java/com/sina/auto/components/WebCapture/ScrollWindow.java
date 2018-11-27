package com.sina.auto.components.WebCapture;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;

public class ScrollWindow {

    protected static void scroll(ChromeDriverEx driver,String height) throws InterruptedException{
        //拉滚动条到最底部
	     for(int i = 0; i<Integer.valueOf(height);i+=75){
	         ((JavascriptExecutor) driver).executeScript("scrollTo(0," +String.valueOf(i) + ")");
	         Thread.currentThread().sleep(200);
	     }
         ((JavascriptExecutor) driver).executeScript("scrollTo(0," + String.valueOf(0) + ")");
    }

    protected static void scroll(ChromeDriver driver){
        //拉滚动条到最底部
        String height = String.valueOf(((JavascriptExecutor) driver).executeScript("return document.body.scrollHeight"));
        for(int i = 100; i<Integer.valueOf(height);i+=100){
            ((JavascriptExecutor) driver).executeScript("scrollTo(0," + String.valueOf(i) + ")");
        }
        ((JavascriptExecutor) driver).executeScript("scrollTo(0," + String.valueOf(0) + ")");
    }
}
