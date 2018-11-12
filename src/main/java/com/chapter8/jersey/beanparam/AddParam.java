package com.chapter8.jersey.beanparam;

import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;

public class AddParam {

	@PathParam("a")
	@DefaultValue("10")
    private int a;
    @PathParam("b")
    @DefaultValue("10")
    private int b;
    //从url路径提取信息
    @MatrixParam("m")
    private String matrixParam;
    //从http头部提取信息
    @HeaderParam("User-Agent")
    private String whichBrowser;
    //从关联http头部的cookie里提取信息
    @CookieParam("sessionid")
    private String sessionid;

	public AddParam() {
		super();
	}

	public AddParam(int a, int b) {
		super();
		this.a = a;
		this.b = b;
	}

	public int getA() {
		return a;
	}

	public int getB() {
		return b;
	}

	public String getWhichBrowser() {
		return whichBrowser;
	}

	public String getSessionid() {
		return sessionid;
	}

}
