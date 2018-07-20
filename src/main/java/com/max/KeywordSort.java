package src.main.java.com.max;

import com.max.CaptchaResp;
import com.max.PCChromeUserAgents;
import com.max.Utils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class KeywordSort {
    private WebDriver driver;
    public static void main(String[] args) {
        KeywordSort ks =new KeywordSort();
        System.setProperty("webdriver.chrome.driver", "chromedriver2.38.exe"); //加载驱动
        String keyword="headset";
        String asin="B0728PHCD8";
        Map<String, String> addressInfo=Utils.getAddressInfo("https://www.fakeaddressgenerator.com/World/us_address_generator");
        String ip=ks.getIp();
        System.out.println(ip);
        ks.openUrl(ip);
        //ks.testCookie();
        ks.createAccount(addressInfo);
        ks.inputKeyword(keyword);
        ks.searchItem(asin);
        ks.addCartOrList();


    }

    public void openUrl(String ip){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-infobars");
        options.addArguments("--start-maximized");
        options.addArguments("--lang=en-US");
        options.addArguments("--user-agent="+PCChromeUserAgents.getAUserAgent());
        options.addArguments("--proxy-server=http://"+ip);
        driver = new ChromeDriver(options);
        driver.get("http://ip138.com/");
        sleep(5);
        driver.get("https://www.amazon.com");
    }

    public void inputKeyword(String keyword){
        WebElement search = driver.findElement(By.id("twotabsearchtextbox"));
        search.sendKeys(keyword);
        search.sendKeys(Keys.ENTER);
        sleep(new Random().nextInt(4)+2);
    }

    public void searchItem(String asin){
        boolean flag=true;
        while (flag){
            WebElement results=driver.findElement(By.id("resultsCol"));
            List<WebElement> itemList=results.findElements(By.tagName("li"));
            for(WebElement item:itemList){
                String dataAsin=item.getAttribute("data-asin");
                if(dataAsin!=null && dataAsin.equals(asin)){
                    System.out.println("Founded!!!!!!!");
                    scroll(driver,item);
                    sleep(2);
                    item.findElement(By.cssSelector(".a-fixed-left-grid-col.a-col-left")).click();
                    flag=false;
                    break;
                }
            }
            if(flag){
                WebElement next=driver.findElement(By.id("pagnNextString"));
                scroll(driver,next);
                sleep(2);
                next.click();
            }
            sleep(new Random().nextInt(4)+2);
        }

    }

    public void addCartOrList(){
        scroll(driver,driver.findElement(By.id("navFooter")));
        sleep(2);
        int number=new Random().nextInt(2);
        if(number==0){
            WebElement addcart=driver.findElement(By.id("add-to-cart-button"));
            scroll(driver,addcart);
            addcart.click();
            sleep(new Random().nextInt(4)+2);
        }else {
            WebElement addlist=driver.findElement(By.id("add-to-wishlist-button-submit"));
            scroll(driver,addlist);
            addlist.click();
            sleep(new Random().nextInt(4)+2);
            driver.findElement(By.xpath("//*[@id=\"WLHUC_result\"]/form/div[2]/span[3]/span/span/input")).click();
            WebElement createList=Utils.getWebElement(driver,By.xpath("//*[@id=\"WLHUC_result\"]/form/div[2]/span[3]/span/span/input"),2);
            if(createList!=null){
                WebElement conshopping= Utils.getWebElement(driver,By.xpath("//*[@id=\\\"wl-huc-post-create-msg\\\"]/div/div[2]/span[2]/span/span/button"),5);
                if(conshopping!=null){
                    conshopping.click();
                }
            }

        }

    }


    public void testCookie(){
        String sql="SELECT cookie FROM account LIMIT 1";
        List<Map<String, Object>> list=Utils.getData(sql);
        String cookieStr= (String) list.get(0).get("cookie");
        JSONArray jsonArray=JSONArray.fromObject(cookieStr);
        for(int i=0;i<jsonArray.size();i++){
            JSONObject job=jsonArray.getJSONObject(i);
            Date date=null;
            JSONObject expiry=job.getJSONObject("expiry");
            if(expiry.containsKey("time")){
                date=new Date(expiry.getLong("time"));
            }
            Cookie c=new Cookie(job.getString("name"),job.getString("value"),job.getString("domain"),job.getString("path"),date,job.getBoolean("secure"),job.getBoolean("httpOnly"));
            driver.manage().addCookie(c);
        }
        driver.get("https://www.amazon.com");
    }

    public void login(String email, String password){
        WebElement ap_email=driver.findElement(By.id("ap_email"));
        ap_email.sendKeys(email);
        ap_email.sendKeys(Keys.ENTER);//driver.findElement(By.id("continue")).click();
        sleep(3);
        WebElement ap_password=driver.findElement(By.id("ap_password"));
        ap_password.sendKeys(password);
        ap_password.sendKeys(Keys.ENTER);//driver.findElement(By.id("signInSubmit")).click();  auth-error-message-box  auth-captcha-image-container  auth-captcha-guess
        sleep(new Random().nextInt(4)+2);
    }

    public void createAccount(Map<String, String> addressInfo){
        String fullname="";
        String email="";
        String[] suffixs = {"gmail.com", "yahoo.com", "aol.com", "hotmail.com", "live.com","msn.com","geocities.com","bing.com","info.com","webmine.com","freespace.com","mail.com","myspace.com","geocities.com","booksmart.com","carmag.com","infoseller.com"};
        String password=Utils.createRandomCharData(new Random().nextInt(5)+8);
        String status=addressInfo.get("status");
        if(status.equals("success")){
            fullname= addressInfo.get("fullname");
            email=fullname.replaceAll(" ","")+Utils.createRandomCharData(new Random().nextInt(3)+4)+"@"+suffixs[new Random().nextInt(suffixs.length)];
        }else{
            System.out.println("获取地址信息错误！");
        }
        WebElement sign =Utils.getWebElement(driver,By.id("nav-link-accountList"),5);
        if(sign==null){
            sign =Utils.getWebElement(driver,By.id("nav-link-yourAccount"),5);
        }
        sign.click();
        sleep(new Random().nextInt(4)+1);
        driver.findElement(By.id("createAccountSubmit")).click();
        sleep(new Random().nextInt(4)+1);
        driver.findElement(By.id("ap_customer_name")).sendKeys(fullname);
        sleep(new Random().nextInt(3)+1);
        driver.findElement(By.id("ap_email")).sendKeys(email);
        sleep(new Random().nextInt(3)+1);
        driver.findElement(By.id("ap_password")).sendKeys(password);
        sleep(new Random().nextInt(3)+1);
        driver.findElement(By.id("ap_password_check")).sendKeys(password);
        sleep(new Random().nextInt(3)+1);
        driver.findElement(By.id("continue")).click();
        WebElement captchaImage=Utils.getWebElement(driver,By.id("auth-captcha-image-container"),5);
        if(captchaImage!=null){
            String path="captchaImg"+new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss")+new Random().nextInt(500)+".jpg";
            Utils.screenShotForElement(driver,captchaImage,path);
            CaptchaResp cap=Utils.authAmzCaptcha(path);
            String code=cap.getPic_str();
            driver.findElement(By.id("auth-captcha-guess")).sendKeys(code);
            driver.findElement(By.id("ap_password")).sendKeys(password);
            driver.findElement(By.id("ap_password_check")).sendKeys(password);
            driver.findElement(By.id("continue")).click();
            sleep(new Random().nextInt(4)+2);
        }
        Set<Cookie> set = driver.manage().getCookies();
        JSONArray json= JSONArray.fromObject(set);
        String cookieStr=json.toString();

        Connection con=Utils.getConnection();
        String sql="INSERT INTO account (customName,email,password,cookie,updateTime) VALUES (?,?,?,?,?)";
        try{
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1,fullname);
            pstmt.setString(2,email);
            pstmt.setString(3,password);
            pstmt.setString(4,cookieStr);
            pstmt.setDate(5, new java.sql.Date(new Date().getTime()));
            pstmt.executeUpdate();
            pstmt.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(con!=null){
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public void sleep(int number){
        try {
            Thread.sleep(number*1000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void scroll(WebDriver driver, WebElement e){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoViewIfNeeded(true);", e);
    }

    public String getIp(){
        String ip="";
        BufferedReader reader = null;
        try {
            URL realUrl = new URL("http://webapi.http.zhimacangku.com/getip?num=1&type=1&pro=&city=0&yys=0&port=1&time=1&ts=0&ys=0&cs=0&lb=1&sb=0&pb=4&mr=1&regions=");
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
            //是否允许输入输出
            conn.setDoInput(true);
            conn.setDoOutput(true);
            // 建立实际的连接
            conn.connect();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                ip += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ip.trim();
    }

}

