package com.sina.auto.components.WebCapture;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CaptureWAP {
    static String chromeDriver = OSname.osname();

    //将整个页面截取出
    public static BufferedImage CaptureFullPage(String url, String zoom,boolean setHeadless) throws Exception {
        int width = 640;
        Double zoomfactor = Double.parseDouble(zoom);
        //配置chromedriver和chrome的路径
        System.setProperty("webdriver.chrome.driver",chromeDriver);//chromedriver服务地址

        //配置ChromeDriver启动参数，关闭Chrome自动化提示
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("disable-infobars");
        options.setHeadless(setHeadless);
        //模拟手机浏览器
        Map<String, Object> deviceMetrics = new HashMap<String, Object>();
        deviceMetrics.put("width", width);
        deviceMetrics.put("height", 900);
        deviceMetrics.put("pixelRatio", zoomfactor);
        Map<String, Object> mobileEmulation = new HashMap<String, Object>();

        mobileEmulation.put("deviceMetrics", deviceMetrics);
        mobileEmulation.put("userAgent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_4 like Mac OS X) AppleWebKit/604.3.5 (KHTML, like Gecko) Version/11.0 MQQBrowser/8.5.1 Mobile/15B87 Safari/604.1 MttCustomUA/2 QBWebViewType/1 WKType/1");

        options.setExperimentalOption("mobileEmulation", mobileEmulation);

        //创建ChromeDriverEx对象，ChromeDriverEx对ChromeDriver进行扩展。
        ChromeDriverEx driver = new ChromeDriverEx(options);

        //打开网页
        driver.get(url);
	    String height = String.valueOf(((JavascriptExecutor) driver).executeScript("return document.body.scrollHeight"));
        ScrollWindow.scroll(driver,height);
        //生成图片
        File file = driver.getFullScreenshotAs(OutputType.FILE,height);
        BufferedImage bi = ImageIO.read(file);
        driver.quit();
        return bi;
    }

    //截取首屏
    public static BufferedImage CaptureCurrentPage (String url, String zoom, boolean setHeadless) throws Exception{
        int width = 640;
        Double zoomfactor = Double.parseDouble(zoom);
        System.setProperty("webdriver.chrome.driver",chromeDriver);//chromedriver服务地址
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("disable-infobars");
        options.setHeadless(setHeadless);
        //模拟手机打开
        Map<String, Object> deviceMetrics = new HashMap<String, Object>();
        deviceMetrics.put("width", width);
        deviceMetrics.put("height", 900);
        deviceMetrics.put("pixelRatio", zoomfactor);
        Map<String, Object> mobileEmulation = new HashMap<String, Object>();

        mobileEmulation.put("deviceMetrics", deviceMetrics);
        mobileEmulation.put("userAgent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_4 like Mac OS X) AppleWebKit/604.3.5 (KHTML, like Gecko) Version/11.0 MQQBrowser/8.5.1 Mobile/15B87 Safari/604.1 MttCustomUA/2 QBWebViewType/1 WKType/1");

        options.setExperimentalOption("mobileEmulation", mobileEmulation);

        ChromeDriver driver = new ChromeDriver(options);

        driver.manage().window().setSize(new Dimension(700, 600));

        driver.get(url);
        ScrollWindow.scroll(driver);

        File file = driver.getScreenshotAs(OutputType.FILE);
        BufferedImage bi = ImageIO.read(file);
        driver.quit();
        return bi;
    }

    //截取页面指定区域
//    public static BufferedImage CapturePartialPage (String url, String zoom, String cropheight, boolean setHeadless) throws Exception{
//        Double zoomfactor = Double.parseDouble(zoom);
//        System.setProperty("webdriver.chrome.driver",chromeDriver);//chromedriver服务地址
//
//        ChromeOptions options = new ChromeOptions();
//        options.setExperimentalOption("useAutomationExtension", false);
//        options.addArguments("disable-infobars");
//        options.setHeadless(setHeadless);
//        //模拟手机打开
//        Map<String, Object> deviceMetrics = new HashMap<String, Object>();
//        deviceMetrics.put("width", 640);
//        deviceMetrics.put("height", 900);
//        deviceMetrics.put("pixelRatio", zoomfactor);
//        Map<String, Object> mobileEmulation = new HashMap<String, Object>();
//
//        mobileEmulation.put("deviceMetrics", deviceMetrics);
//        mobileEmulation.put("userAgent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_4 like Mac OS X) AppleWebKit/604.3.5 (KHTML, like Gecko) Version/11.0 MQQBrowser/8.5.1 Mobile/15B87 Safari/604.1 MttCustomUA/2 QBWebViewType/1 WKType/1");
//
//        options.setExperimentalOption("mobileEmulation", mobileEmulation);
//
//        ChromeDriverEx driver = new ChromeDriverEx(options);
//
//        driver.manage().window().setSize(new Dimension(700, 600));
//
//        driver.get(url);
//        ScrollWindow.scroll(driver);
//
//        File file = driver.getPartialScreenshotAs(OutputType.FILE, "640",cropheight);
//        BufferedImage bi = ImageIO.read(file);
//        driver.quit();
//        return bi;
//    }

}
