package com.max;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import src.main.java.com.max.KeywordSort;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Test {

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "chromedriver2.38.exe"); //加载驱动
        src.main.java.com.max.KeywordSort ks=new KeywordSort();
        String ip=ks.getIp();
        System.out.println(ip);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-infobars");
        options.addArguments("--start-maximized");
        options.addArguments("--lang=en-US");
        options.addArguments("--user-agent="+PCChromeUserAgents.getAUserAgent());
        options.addArguments("--proxy-server=http://"+ip);
        WebDriver driver = new ChromeDriver(options);
        driver.get("http://ip138.com/");
        driver.get("https://www.amazon.com");
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(1531999730954L)));
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(1532054766000L)));
        System.out.println(new Date().getTime());

    }


}
