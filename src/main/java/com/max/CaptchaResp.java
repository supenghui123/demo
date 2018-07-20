package com.max;

public class CaptchaResp {
	private String err_no;//返回代码
    private String err_str; //中文描述的返回信息
    private String pic_id; //图片标识ID,推荐按字符串类型保存图片ID
    private String pic_str; //识别出的结果
    private String md5; //md5校验值,用来校验此条数据返回是否真实有效,算法: md5(软件ID,软件KEY,图片ID,图片结果), 如果算出来的值与返回的值不一致,那么此条数据极有可能被篡改
    private String str_debug;//开发者自定义信息
    
    public CaptchaResp(String postPicResp){
    	String [] temp = postPicResp.split(",\"");    	
    	err_no = temp[0].split(":")[1].replaceAll("\"", "");
    	err_str = temp[1].split(":")[1].replaceAll("\"", "");
    	pic_id = temp[2].split(":")[1].replaceAll("\"", "");
    	pic_str = temp[3].split(":")[1].replaceAll("\"", "");
    	md5 = temp[4].split(":")[1].replaceAll("\"", "");
    	str_debug = temp[5].split(":")[1].replaceAll("\"", "");    	
    }
    //
	public String getErr_no() {
		return err_no;
	}
	public void setErr_no(String err_no) {
		this.err_no = err_no;
	}
	public String getErr_str() {
		return err_str;
	}
	public void setErr_str(String err_str) {
		this.err_str = err_str;
	}
	public String getPic_id() {
		return pic_id;
	}
	public void setPic_id(String pic_id) {
		this.pic_id = pic_id;
	}
	public String getPic_str() {
		return pic_str;
	}
	public void setPic_str(String pic_str) {
		this.pic_str = pic_str;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String getStr_debug() {
		return str_debug;
	}
	public void setStr_debug(String str_debug) {
		this.str_debug = str_debug;
	}
    
    
}
