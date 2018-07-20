package com.max;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;


public class AuthCaptcha {
	/**
	 * 字符串MD5加密
	 * @param s 原始字符串
	 * @return  加密后字符串
	 */
	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 通用POST方法
	 * @param url 		请求URL
	 * @param param 	请求参数，如：username=test&password=1
	 * @return			response
	 * @throws IOException
	 */
	public static String httpRequestData(String url, String param)
			throws IOException {
		URL u;
		HttpURLConnection con = null;
		OutputStreamWriter osw;
		StringBuffer buffer = new StringBuffer();

		u = new URL(url);
		con = (HttpURLConnection) u.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");

		osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
		osw.write(param);
		osw.flush();
		osw.close();

		BufferedReader br = new BufferedReader(new InputStreamReader(con
				.getInputStream(), "UTF-8"));
		String temp;
		while ((temp = br.readLine()) != null) {
			buffer.append(temp);
			buffer.append("\n");
		}

		return buffer.toString();
	}

	/**
	 * 查询题分
	 * @param username	用户名
	 * @param password	密码
	 * @return			response
	 * @throws IOException
	 */
	public static String GetScore(String username, String password) {
		String param = String.format("user=%s&pass=%s", username, password);
		String result;
		try {
			result = httpRequestData(
					"http://code.chaojiying.net/Upload/GetScore.php", param);
		} catch (IOException e) {
			result = "未知问题";
		}
		return result;
	}
	
	/**
	 * 注册账号
	 * @param username	用户名
	 * @param password	密码
	 * @return			response
	 * @throws IOException
	 */
	public static String UserReg(String username, String password) {
		String param = String.format("user=%s&pass=%s", username, password);
		String result;
		try {
			result = httpRequestData(
					"http://code.chaojiying.net/Upload/UserReg.php", param);
		} catch (IOException e) {
			result = "未知问题";
		}
		return result;
	}

	/**
	 * 账号充值
	 * @param username	用户名
	 * @param card		卡号
	 * @return			response
	 * @throws IOException
	 */
	public static String UserPay(String username, String card) {

		String param = String.format("user=%s&card=%s", username, card);
		String result;
		try {
			result = httpRequestData(
					"http://code.chaojiying.net/Upload/UserPay.php", param);
		} catch (IOException e) {
			result = "未知问题";
		}
		return result;
	}
	
	/**
	 * 报错返分
	 * @param username	用户名
	 * @param password	用户密码
	 * @param softId	软件ID
	 * @param id		图片ID
	 * @return			response
	 * @throws IOException
	 */
	public static String ReportError(String username, String password, String softid, String id) {
		
		String param = String
		.format(
				"user=%s&pass=%s&softid=%s&id=%s",
				username, password, softid, id);
		String result;
		try {
			result = httpRequestData(
					"http://code.chaojiying.net/Upload/ReportError.php", param);
		} catch (IOException e) {
			result = "未知问题";
		}
		
		return result;
	}


	/**
	 * 核心上传函数
	 * @param url 			请求URL
	 * @param param			请求参数，如：username=test&password=1
	 * @param data			图片二进制流
	 * @return				response
	 * @throws IOException
	 */
	public static String httpPostImage(String url, String param,
			byte[] data) throws IOException {
		long time = (new Date()).getTime();
		URL u = null;
		HttpURLConnection con = null;
		String boundary = "----------" + MD5(String.valueOf(time));
		String boundarybytesString = "\r\n--" + boundary + "\r\n";
		OutputStream out = null;
		
		u = new URL(url);
		
		con = (HttpURLConnection) u.openConnection();
		con.setRequestMethod("POST");
		//con.setReadTimeout(60000);   
		con.setConnectTimeout(60000);
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(true);
		con.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + boundary);
		
		out = con.getOutputStream();
			
		for (String paramValue : param.split("[&]")) {
			out.write(boundarybytesString.getBytes("UTF-8"));
			String paramString = "Content-Disposition: form-data; name=\""
					+ paramValue.split("[=]")[0] + "\"\r\n\r\n" + paramValue.split("[=]")[1];
			out.write(paramString.getBytes("UTF-8"));
		}
		out.write(boundarybytesString.getBytes("UTF-8"));

		String paramString = "Content-Disposition: form-data; name=\"userfile\"; filename=\""
				+ "chaojiying_java.gif" + "\"\r\nContent-Type: application/octet-stream\r\n\r\n";
		out.write(paramString.getBytes("UTF-8"));
		
		out.write(data);
		
		String tailer = "\r\n--" + boundary + "--\r\n";
		out.write(tailer.getBytes("UTF-8"));

		out.flush();
		out.close();

		StringBuffer buffer = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(con
					.getInputStream(), "UTF-8"));
		String temp;
		while ((temp = br.readLine()) != null) {
			buffer.append(temp);
			buffer.append("\n");
		}

		return buffer.toString();
	}	
	
	/**
	 * 识别图片_按图片文件路径
	 * @param username		用户名
	 * @param password		密码
	 * @param softid		软件ID
	 * @param codetype		图片类型

	 * @param len_min		最小位数
	 * @param time_add		附加时间
	 * @param str_debug		开发者自定义信息
	 * @param filePath		图片文件路径
	 * @return
	 * @throws IOException
	 */
	public static String PostPic(String username, String password,
			String softid, String codetype, String len_min, String time_add, String str_debug,
			String filePath) {
		String result = "";
		String param = String
		.format(
				"user=%s&pass=%s&softid=%s&codetype=%s&len_min=%s&time_add=%s&str_debug=%s",
				username, password, softid, codetype, len_min, time_add, str_debug);
		try {
				byte[] data = readImg(filePath);				
				if (data.length > 0)
					result = httpPostImage("http://upload.chaojiying.net/Upload/Processing.php", param, data);
			
		} catch(Exception e) {
			result = "-1";
		}		
		
		return result;
	}

	/**
	 * 识别图片_按图片二进制流
	 * @param username		用户名
	 * @param password		密码
	 * @param softid		软件ID
	 * @param codetype		图片类型

	 * @param len_min		最小位数
	 * @param time_add		附加时间
	 * @param str_debug		开发者自定义信息
	 * @param byteArr		图片二进制数据流
	 * @return
	 * @throws IOException
	 */
	public static String PostPic(String username, String password,
			String softid, String codetype, String len_min, String time_add, String str_debug,
			byte[] byteArr) {
		String result = "";
		String param = String
		.format(
				"user=%s&pass=%s&softid=%s&codetype=%s&len_min=%s&time_add=%s&str_debug=%s",
				username, password, softid, codetype, len_min, time_add, str_debug);
		try {
			result = httpPostImage("http://upload.chaojiying.net/Upload/Processing.php", param, byteArr);
		} catch(Exception e) {
			result = "-1";
		}
		
		
		return result;
	}
	
	public static byte[] readImg(String urlOrPath){

        //Logger logger = Logger.getLogger(AuthCaptcha.class);
        byte[] imgBytes = new byte[1024 * 1024];

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream reader = null;
        InputStream in = null;        
        File temFile = null;

        try {
            if(urlOrPath.startsWith("https")){
            	URL imgUrl = new URL(urlOrPath);
                HttpsURLConnection conn = (HttpsURLConnection)imgUrl.openConnection();
                temFile = new File(new Date().getTime() + ".jpg");
                FileOutputStream tem = new FileOutputStream(temFile);
                BufferedImage image = ImageIO.read(conn.getInputStream());
                ImageIO.write(image, "jpg", tem);
                in = new FileInputStream(temFile);                
            }else if(urlOrPath.startsWith("http")){            	
            	URL imgUrl = new URL(urlOrPath);
                URLConnection conn = imgUrl.openConnection();
                temFile = new File(new Date().getTime() + ".jpg");
                FileOutputStream tem = new FileOutputStream(temFile);
                BufferedImage image = ImageIO.read(conn.getInputStream());
                ImageIO.write(image, "jpg", tem);
                in = new FileInputStream(temFile);
            }else{
            	File imgFile = new File(urlOrPath);
                if(!imgFile.isFile() || !imgFile.exists() || !imgFile.canRead()){
                    //logger.info("图片不存在或不可读");
					System.out.println("图片不存在或不可读");
                    return new byte[0];
                }
                in = new FileInputStream(imgFile);
            }
            reader = new BufferedInputStream(in);
            byte[] buffer = new byte[1024];
            while(reader.read(buffer) != -1){
                out.write(buffer);
            }
            imgBytes = out.toByteArray();

        } catch (Exception e) {
            //logger.error("读取图片发生异常", e);
			System.out.println("读取图片发生异常");
        }finally{
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {                    
                    e.printStackTrace();
                }
            }
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {                    
                    e.printStackTrace();
                }
            }
            try {
                out.close();
            } catch (IOException e) {                
                e.printStackTrace();
            }
            if(temFile != null){
                temFile.delete();
            }
        }
        return imgBytes;
    }

}
