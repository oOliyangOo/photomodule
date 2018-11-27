package com.sina.auto.components.WebCapture;

import com.google.common.collect.ImmutableMap;
import com.sina.auto.components.WebCapture.api.google.ScheduledService;
import com.sina.auto.components.WebCapture.api.google.Singleton;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.HasTouchScreen;
import org.openqa.selenium.interactions.TouchScreen;
import org.openqa.selenium.os.WindowsUtils;
import org.openqa.selenium.remote.CommandInfo;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.http.HttpMethod;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.Map;

/*
 *继承谷歌驱动，通过cmd的方式，截取图片
 *
 *
*/
public class ChromeDriverEx extends ChromeDriver implements HasTouchScreen {

/*	private volatile static ChromeDriverEx chromeDriverEx;
	public static ChromeDriverEx getInstance(ChromeDriverService service,ChromeOptions options){
		if(chromeDriverEx == null){
			synchronized(ChromeDriverEx.class){
				if(chromeDriverEx == null){
					try {
						chromeDriverEx = new ChromeDriverEx(service,options);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						ScheduledService.jsq=0;
					}
				}
			}
		}
		return chromeDriverEx;
	}*/
	
    public ChromeDriverEx(ChromeOptions options) throws Exception {
        this(ChromeDriverService.createDefaultService(), options);
    }
    public ChromeDriverEx(ChromeDriverService service, ChromeOptions options) throws Exception {
        super(service, options);
        CommandInfo cmd = new CommandInfo("/session/:sessionId/chromium/send_command_and_get_result", HttpMethod.POST);
        Method defineCommand = HttpCommandExecutor.class.getDeclaredMethod("defineCommand", String.class, CommandInfo.class);
        defineCommand.setAccessible(true);
        defineCommand.invoke(super.getCommandExecutor(), "sendCommand", cmd);
    }

    @SuppressWarnings("unchecked")
	public <X> X getFullScreenshotAs(OutputType<X> outputType,String height) throws Exception {
    	if(Integer.parseInt(String.valueOf(height))>9500) {
    		height=String.valueOf(Integer.parseInt(String.valueOf(height))*3/4);
    	}
        Object metrics = sendEvaluate(
                "({" +
                        "width: Math.max(window.innerWidth,document.body.scrollWidth,document.documentElement.scrollWidth)|0," +
                        "height: "+height+"|0," +
                        "deviceScaleFactor: window.devicePixelRatio || 1," +
                        "mobile: typeof window.orientation !== 'undefined'" +
                        "})");
        sendCommand("Emulation.setDeviceMetricsOverride", metrics);
        Object result = sendCommand("Page.captureScreenshot", ImmutableMap.of("format", "png", "fromSurface", true));
        try {
        	sendCommand("Emulation.clearDeviceMetricsOverride", ImmutableMap.of());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
        String base64EncodedPng = (String)((Map<String, ?>)result).get("data");
        return outputType.convertFromBase64Png(base64EncodedPng);
    }

    protected Object sendCommand(String cmd, Object params) {
    	try {
            return execute("sendCommand", ImmutableMap.of("cmd", cmd, "params", params)).getValue();
		} catch (Exception e) {
			// TODO: handle exception
			ScheduledService.jsq=0;
			e.printStackTrace();
			return null;
		}
    }

    @SuppressWarnings("unchecked")
	protected Object sendEvaluate(String script) {
    	try {
            Object response = sendCommand("Runtime.evaluate", ImmutableMap.of("returnByValue", true, "expression", script));
            Object result = ((Map<String, ?>)response).get("result");
            return ((Map<String, ?>)result).get("value");
		} catch (Exception e) {
			// TODO: handle exception
			ScheduledService.jsq=0;
			e.printStackTrace();
			return null;
		}
    }

	@Override
	public TouchScreen getTouch() {
		// TODO Auto-generated method stub
		return null;
	}
}
