package com.sina.auto.components.WebCapture;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.os.WindowsUtils;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import java.lang.management.ManagementFactory;

import com.sina.auto.components.WebCapture.api.google.ScheduledService;
import com.sina.auto.components.WebCapture.api.google.Singleton;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import static com.sina.auto.components.WebCapture.OSname.osname;
import static com.sina.auto.components.WebCapture.ScrollWindow.scroll;

public class CapturePC {
    static String chromeDriver = osname();

    //将整个页面截取出
    public static Map<String,Object> CaptureFullPage(String url, String zoom,boolean setHeadless) throws Exception {
        //配置chromedriver和chrome的路径
    	ChromeDriverService service = new ChromeDriverService.Builder().usingDriverExecutable(new File("D:\\tomcat\\apache-tomcat-8.5.34\\bin\\chromedriver\\Windows\\chromedriver.exe")).usingAnyFreePort().build();
    	/*ChromeDriverService service = new ChromeDriverService.Builder().usingDriverExecutable(new File("X:\\eclipse-workspace\\photomodule\\chromedriver\\Windows\\chromedriver.exe")).usingAnyFreePort().build();*/
    	service.start();

        //创建ChromeDriverEx对象，ChromeDriverEx对ChromeDriver进行扩展。
    	ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("disable-infobars");
        options.addArguments("--no-sandbox");
        options.addArguments("--incognito");
        options.setHeadless(setHeadless);
        ChromeDriverEx driver = new ChromeDriverEx(service,options);
    	/*ChromeDriverEx driver = ChromeDriverEx.getInstance(service,options);*/
        if(!url.contains(".pdf")) {
            //打开网页，通过线程，控制超时
        	String height="0";
        	ExecutorService executorService = Executors.newFixedThreadPool(1);
			Callable<String> call = new Callable<String>() {
		        public String call() throws Exception {
		        	driver.manage().timeouts().pageLoadTimeout(60,TimeUnit.SECONDS);
		        	driver.get(url);
		    	    String height = String.valueOf(((JavascriptExecutor) driver).executeScript("return document.body.scrollHeight"));
		    	    if(driver.getSessionId()!=null) {
		    	    	scroll(driver,height);
		    	    }else {
		    	    	return "";
		    	    }
		    	    Thread.sleep(1000);
		            return "线程执行完成.";  
		         }  
	        };
	        try {  
	            Future<String> future = executorService.submit(call);  
	            String obj = future.get(60000, TimeUnit.MILLISECONDS); //任务处理超时时间设为 1 秒  
	            System.out.println("任务成功返回:" + obj);  
	        } catch (Exception e) {  
	        	ScheduledService.jsq=0;
	            System.out.println("处理失败.");
	            System.out.println("失败网址========================"+url);
	            driver.close();
	            //driver.quit();
	            service.stop();
	            e.printStackTrace();
	            return null;
	        }
	        try {
	    	    //生成图片
	            File file = driver.getFullScreenshotAs(OutputType.FILE,height);
	            BufferedImage bi = ImageIO.read(file);
	            Map<String, Object> mp=new HashMap<String,Object>();
	            mp.put("bi", bi);
	            driver.close();
	            //driver.quit();
	            service.stop();
	            return mp;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return null;
			}
        }else {
        	byte[] b=DownloadPdf.downLoadByUrl(url, url.substring(url.indexOf("/"), url.indexOf(".pdf")+4), "D:/");
        	Map<String, Object> mp=new HashMap<String,Object>();
            mp.put("image", b);
            
            return mp;
        }
    }

/*    //截取首屏
    public static BufferedImage CaptureCurrentPage (String url, String zoom, boolean setHeadless) throws Exception{
        System.setProperty("webdriver.chrome.driver",chromeDriver);//chromedriver服务地址
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("disable-infobars");
        options.setHeadless(setHeadless);
        ChromeDriver driver = new ChromeDriver(options);

        driver.manage().window().setSize(new Dimension(700, 600));

        driver.manage().timeouts().pageLoadTimeout(3,TimeUnit.SECONDS);
        driver.get(url);

        File file = driver.getScreenshotAs(OutputType.FILE);
        BufferedImage bi = ImageIO.read(file);
        driver.close();
        return bi;
    }*/

    //截取页面指定区域
//    public static BufferedImage CapturePartialPage (String url, String zoom, String cropheight, boolean setHeadless) throws Exception{
//        System.setProperty("webdriver.chrome.driver",chromeDriver);//chromedriver服务地址
//
//        ChromeOptions options = new ChromeOptions();
//        options.setExperimentalOption("useAutomationExtension", false);
//        options.addArguments("disable-infobars");
//        options.setHeadless(setHeadless);
//
//        ChromeDriverEx driver = new ChromeDriverEx(options);
//
//        driver.manage().window().setSize(new Dimension(700, 600));
//
//        driver.manage().timeouts().pageLoadTimeout(3,TimeUnit.SECONDS);
//        driver.get(url);
//
//        File file = driver.getPartialScreenshotAs(OutputType.FILE, "640",cropheight);
//        BufferedImage bi = ImageIO.read(file);
//        driver.quit();
//        return bi;
//    }

}
