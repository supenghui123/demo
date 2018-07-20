package com.max;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

public class Utils {

    public static  Connection getConnection(){
        String url = "jdbc:mysql://localhost:3306/amazon";
        String username = "root";
        String password = "root";
        Connection con = null;
        boolean flag=true;
        while(flag){
            try {
                Class.forName("com.mysql.jdbc.Driver");
                con = DriverManager.getConnection(url,username,password);
                flag=false;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return con;
    }

    public static List<Map<String, Object>> getData(String sql) {
        List<Map<String, Object>> list=new ArrayList<>();
        //Connection con = Utils.getConnection();
        Connection con=getSqliteCon();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt=con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            ResultSetMetaData md = rs.getMetaData(); //获得结果集结构信息,元数据
            int columnCount = md.getColumnCount();   //获得列数
            while (rs.next()) {
                Map<String,Object> rowData = new HashMap<String,Object>();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), rs.getObject(i));
                }
                list.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                rs.close();
                pstmt.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static WebElement getWebElement(WebDriver driver, By by, long timeoutSeconds){
        WebDriverWait wait = new WebDriverWait(driver, timeoutSeconds);
        final By localBy = by;
        try{
            wait.until(new ExpectedCondition<WebElement>() {
                public WebElement apply(WebDriver d) {
                    return d.findElement(localBy);
                }
            });
            return driver.findElement(localBy);
        }catch (Exception e){
            return null;
        }

    }

    public static void inputWord(WebElement e, String content){
        char[] chars=content.toCharArray();
        for(char c:chars){
            e.sendKeys(String.valueOf(c));
            try {
                Thread.sleep(new Random().nextInt(9)*100+100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static WebDriver getMobileDriver(String proxyPath){
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> deviceMetrics = new HashMap<>();
        deviceMetrics.put("width", 360);
        deviceMetrics.put("height", 640);
        deviceMetrics.put("pixelRatio", 3.0);

        Map<String, Object> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceMetrics", deviceMetrics);
        System.out.println(PCChromeUserAgents.getMBUserAgent());
        mobileEmulation.put("userAgent", PCChromeUserAgents.getMBUserAgent());
        Map<String, Object> prefs = new HashMap<>();
        // 设置提醒的设置，2表示block
        prefs.put("profile.default_content_setting_values.notifications", 2);
        //prefs.put("profile.default_content_setting_values.images", 2);

        options.setExperimentalOption("mobileEmulation", mobileEmulation);
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("disable-infobars");
        //options.addExtensions(new File(proxyPath));
        WebDriver driver=new ChromeDriver(options);
        driver.manage().window().setSize(new Dimension(360, 640));
        return driver;
    }

    public static void scroll(WebDriver driver, WebElement e){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoViewIfNeeded(true);", e);
    }

    public static void dropDown(WebDriver driver, List<WebElement> list, String text){
        for(WebElement we:list){
            if(we.getText().equals(text)){
                if(!we.isDisplayed()){
                    scroll(driver,we);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                we.click();
                break;
            }
        }
    }
    
    //截全屏
    public static void captureScreenshot(WebDriver driver, String fileName) {
        String imagePath = System.getProperty("user.dir") + File.separator + fileName + ".png";
        try {
            byte[] decodedScreenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            FileOutputStream fos = new FileOutputStream(new File(imagePath));
            fos.write(decodedScreenshot);
            fos.close();
            System.out.println("截图保存至" + imagePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //截取指定元素区域的图片
    public static void screenShotForElement(WebDriver driver,WebElement element, String path) {
        //截取整个页面的图片
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            //获取元素在所处frame中位置对象
            Point p = element.getLocation();
            //获取元素的宽与高
            int width = element.getSize().getWidth();
            int height = element.getSize().getHeight();
            //矩形图像对象
            Rectangle rect = new Rectangle(width, height);
            BufferedImage img = ImageIO.read(scrFile);
            //x、y表示加上当前frame的左边距,上边距
            BufferedImage dest = img.getSubimage(p.getX(), p.getY(), rect.width, rect.height);
            ImageIO.write(dest, "png", scrFile);
            FileUtils.copyFile(scrFile, new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static String getProxyZipPath(String country,String zipDir) throws Exception{
//        double sessionId=Math.random();
//        String zipPath=zipDir+"/"+sessionId+"proxy.zip";
//        String content="{\"version\": \"1.0.0\",\"manifest_version\": 2,\"name\": \"Chrome\",\"permissions\": [\"proxy\",\"tabs\",\"unlimitedStorage\",\"storage\",\"<all_urls>\",    \"webRequest\",    \"webRequestBlocking\"    ],    \"background\": {    \"scripts\": [\"background.js\"]    },    \"minimum_chrome_version\":\"22.0.0\"    }";
//        byte[] buffer =content.getBytes();
//        System.out.println(sessionId);
//        String user="lum-customer-"+Constant.proxyName+"-zone-residential-country-"+country+"-session-"+sessionId;//residential
//        byte[] buffer1 =getJs(Constant.proxyHost,Constant.proxyPort,user,Constant.proxyPwd,"http").getBytes();
//
//        FileOutputStream fOutputStream = new FileOutputStream(new File(zipPath));
//        ZipOutputStream zoutput = new ZipOutputStream(fOutputStream);
//        ZipEntry zEntry  = new ZipEntry("manifest.json");
//        zoutput.putNextEntry(zEntry);
//        zoutput.write(buffer);
//        zoutput.closeEntry();
//        ZipEntry zEntry1  = new ZipEntry("background.js");
//        zoutput.putNextEntry(zEntry1);
//        zoutput.write(buffer1);
//        zoutput.closeEntry();
//        zoutput.close();
//        fOutputStream.close();
//        return zipPath;
//    }

    public static String getJs(String host, String port, String user, String password, String scheme){
        String js="var config = {    mode: \"fixed_servers\",    rules: {    singleProxy: {    scheme:\""+scheme+"\",    host: \""+host+"\",    port: parseInt("+port+")    },    bypassList: [\"foobar.com\"]    }    };    chrome.proxy.settings.set({value: config, scope: \"regular\"}, function() {});    function callbackFn(details) {    return {    authCredentials: {    username: \""+user+"\",    password: \""+password+"\"    }    };    }    chrome.webRequest.onAuthRequired.addListener(    callbackFn,    {urls: [\"<all_urls>\"]},        ['blocking']        );";
        return js;
    }

    public static Properties readProp(String propPath){
        Properties prop = null;
        InputStream in = null;
        try {
            in= new FileInputStream(new File(propPath));
            prop = new Properties();
            prop.load(in);
        } catch (Exception ex) {
            System.out.println("The DataBase Config File Is Null Or False");
        } finally {
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }

    public static String httpRequest(String url, String method, String params){
        String result="";
        BufferedReader reader = null;
        OutputStreamWriter writer=null;
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestMethod(method);// 提交模式
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
            //是否允许输入输出
            conn.setDoInput(true);
            conn.setDoOutput(true);
            // 建立实际的连接
            conn.connect();
            if(method.equals("POST")){
                writer = new OutputStreamWriter(conn.getOutputStream());
                //发送参数
                writer.write(params);
                //清理当前编辑器的左右缓冲区，并使缓冲区数据写入基础流
                writer.flush();
            }
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(writer!=null){
                try {
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(reader!=null){
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String getIp(String pageSource){
        String ip="no ip";
        if(pageSource.contains("origin")){
            ip=pageSource.substring(pageSource.indexOf("origin"));
            ip=ip.replaceAll("\"","");
            ip=ip.substring(ip.indexOf(":")+1,ip.indexOf(","));
        }
        return ip;
    }

    public static Map<String, String > getAddressInfo(String url){
        Map<String, String> map=new HashMap<>();
        int errorNumber=0;
        while(errorNumber<3){
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36")
                        .cookie("auth", "token")
                        .timeout(60000).get();
                String fullname=doc.getElementsByAttributeValue("class","table common-table").first().getElementsByTag("strong").first().text().trim();
                if(fullname.contains(" ") && fullname.indexOf(" ")!=fullname.lastIndexOf(" ")){
                    fullname=fullname.substring(0,fullname.indexOf(" "))+" "+fullname.substring(fullname.lastIndexOf(" ")+1);
                }else if(fullname.contains(" ")){
                    fullname=fullname.replace(" "," ");
                }
                List<Element> rowList=doc.getElementsByAttributeValue("class","no-style");
                //String fullname=rowList.get(0).attr("value");
                String street=rowList.get(0).attr("value");
                String city=rowList.get(1).attr("value");
                String state=rowList.get(3).attr("value");
                String zipcode=rowList.get(4).attr("value");
                String phone=rowList.get(5).attr("value");
                System.out.println(fullname+"\n"+street+"\n"+city+"\n"+state+"\n"+zipcode+"\n"+phone);
                map.put("status","success");
                map.put("fullname",fullname.trim());
                map.put("street",street);
                map.put("city",city);
                map.put("state",state);
                map.put("zipcode",zipcode);
                map.put("phone",phone);
                break;
            } catch (Exception e) {
                errorNumber++;
                map.put("status","error");
            }
        }
        return map;
    }

    public static String createRandomCharData(int number){
        String result="";
        StringBuilder sb=new StringBuilder();
        Random rand=new Random();//随机用以下三个随机生成器
        Random randdata=new Random();
        int data=0;
        for(int i=0;i<number;i++){
            int index=rand.nextInt(3); //目的是随机选择生成数字，大小写字母
            switch(index){
                case 0:
                    data=randdata.nextInt(10);//仅仅会生成0~9
                    sb.append(data);
                    break;
                case 1:
                    data=randdata.nextInt(26)+65;//保证只会产生65~90之间的整数
                    sb.append((char)data);
                    break;
                case 2:
                    data=randdata.nextInt(26)+97;//保证只会产生97~122之间的整数
                    sb.append((char)data);
                    break;
            }
        }
        result=sb.toString();
        return result;
    }
    
    public static void makedirs(String path){
    	File f=new File(path);
        if(!f.exists()){
        	f.mkdirs();
        }
    }
    
    //如果文件存在，则追加内容；如果文件不存在，则创建文件
    public static void writeText(String path, String logText){
    	FileWriter fw=null;
    	PrintWriter pw=null;
    	try{
    		File f=new File(path);
        	fw =new FileWriter(f, true);
        	pw = new PrintWriter(fw);  
            pw.println(logText);
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		try{
    			if(pw!=null){
        			pw.flush();
        			if(fw!=null){
        				fw.flush();
        				fw.close();
        			}
        			pw.close();
        		}
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    		
    	}
    	
    }

    public static CaptchaResp authAmzCaptcha(String imgUrl){
        try{
            String username = "Kevin2016";
            String password = "poiKL:890)(*";
            String softid = "892986";
            String codetype = "1006";
            String len_min = "0";
            String time_add = "0";
            String resp = AuthCaptcha.PostPic(username, password, softid, codetype, len_min, time_add, "168", imgUrl);
            CaptchaResp captcha = new CaptchaResp(resp);
            return captcha;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static Connection getSqliteCon(){
        Connection con=null;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:D:\\sqlite\\amazon.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }

    public static void main(String[] args) {
        Connection con=getSqliteCon();
        String sql="select * from account";
        List<Map<String, Object>> list=getData(sql);
        for(int i=0;i<list.size();i++){
            System.out.println(list.get(i).get("email"));
            System.out.println(list.get(i).get("password"));
            System.out.println(list.get(i).get("cookie"));
            System.out.println(list.get(i).get("updateTime"));
        }

    }

}
