package com.max;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import src.main.java.com.max.KeywordSort;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class Test {

    public static void main(String[] args) throws ParseException {
       /* System.setProperty("webdriver.chrome.driver", "chromedriver2.38.exe"); //加载驱动
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
        System.out.println(new Date().getTime());*/



        System.setProperty("webdriver.chrome.driver", "chromedriver2.38.exe"); //加载驱动
        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-infobars");
        options.addArguments("--start-maximized");
        options.addArguments("--lang=en-GB");
        options.addArguments("--user-agent="+PCChromeUserAgents.getAUserAgent());
        Map<String, Object> prefs = new HashMap<String, Object>();
        // 设置提醒的设置，2表示block
        //prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("profile.default_content_setting_values.images", 2);
        options.setExperimentalOption("prefs", prefs);
        WebDriver driver=new ChromeDriver(options);
        driver.get("https://www.amazon.co.uk");
        WebElement search =driver.findElement(By.id("twotabsearchtextbox"));
        search.sendKeys("wifi camera");
        search.sendKeys(Keys.ENTER);
        WebElement results=driver.findElement(By.id("resultsCol"));
        List<WebElement> itemList=results.findElements(By.tagName("li"));
        for(WebElement item:itemList){
            String dataAsin=item.getAttribute("data-asin");
            System.out.println(dataAsin);
            if(dataAsin!=null && dataAsin.equals("B0757F8YMF")){
                System.out.println("found");
            }
        }
        System.out.println("yes");

        /*WebElement robotCheck=Utils.getWebElement(driver,By.id("captchacharacters"),5);
        if(robotCheck!=null){
            String imgUrl = robotCheck.findElement(By.xpath("../../div[1]/img")).getAttribute("src");
            CaptchaResp captchaResp = Utils.authAmzCaptcha(imgUrl);
            robotCheck.sendKeys(captchaResp.getPic_str());
            robotCheck.sendKeys(Keys.ENTER);
        }
        String fullname=Utils.createRandomCharData(new Random().nextInt(4)+5)+" "+ Utils.createRandomCharData(new Random().nextInt(5)+6);
        String email="";
        String[] suffixs = {"gmail.com", "yahoo.com", "aol.com", "hotmail.com", "live.com","msn.com","geocities.com","bing.com","info.com","webmine.com","freespace.com","mail.com","myspace.com","geocities.com","booksmart.com","carmag.com","infoseller.com"};
        String password=Utils.createRandomCharData(new Random().nextInt(5)+8);
        email=fullname.replaceAll(" ","")+Utils.createRandomCharData(new Random().nextInt(3)+4)+"@"+suffixs[new Random().nextInt(suffixs.length)];
        WebElement sign =Utils.getWebElement(driver,By.id("nav-link-accountList"),5);
        if(sign==null){
            sign =Utils.getWebElement(driver,By.id("nav-link-yourAccount"),5);
        }
        sign.click();
        WebElement createAccountSubmit=Utils.getWebElement(driver,By.id("createAccountSubmit"),10);
        if(createAccountSubmit==null){
            System.out.println("没有找到创建账号的元素：createAccountSubmit");
        }
        createAccountSubmit.click();
        WebElement customerName=Utils.getWebElement(driver,By.id("ap_customer_name"),10);
        if(customerName==null){
            System.out.println("123");
        }
        driver.findElement(By.id("ap_customer_name")).sendKeys(fullname);
        driver.findElement(By.id("ap_email")).sendKeys(email);
        driver.findElement(By.id("ap_password")).sendKeys(password);
        driver.findElement(By.id("ap_password_check")).sendKeys(password);
        driver.findElement(By.id("continue")).click();
        WebElement captchaImage=Utils.getWebElement(driver,By.id("auth-captcha-image-container"),5);
        if(captchaImage!=null){
            String path2="captchaImg"+new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss").format(new Date())+new Random().nextInt(500)+".jpg";
            //Utils.screenShotForElement(driver,captchaImage,path);
            String path=driver.findElement(By.id("auth-captcha-image")).getAttribute("src");
            CaptchaResp cap=Utils.authAmzCaptcha(path);
            String code=cap.getPic_str();
            System.out.println("check code is:"+code);
            driver.findElement(By.id("auth-captcha-guess")).sendKeys(code);
            driver.findElement(By.id("ap_password")).sendKeys(password);
            driver.findElement(By.id("ap_password_check")).sendKeys(password);
            driver.findElement(By.id("continue")).click();
        }*/


    }


}
