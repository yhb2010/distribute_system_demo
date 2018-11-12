package com.chapter8.jersey.beanparam;

import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;

public class AddParam2 {

	@PathParam("c")
	@DefaultValue("10")
    private int c;
    @PathParam("d")
    @DefaultValue("10")
    private int d;
    //从url路径提取信息
    @MatrixParam("m")
    private String matrixParam;
    //从http头部提取信息
    @HeaderParam("User-Agent")
    private String whichBrowser;
    //从关联http头部的cookie里提取信息
    @CookieParam("sessionid")
    private String sessionid;

	public AddParam2() {
		super();
	}

	public AddParam2(int c, int d) {
		super();
		this.c = c;
		this.d = d;
	}

	public int getC() {
		return c;
	}

	public int getD() {
		return d;
	}

	public String getWhichBrowser() {
		return whichBrowser;
	}

	public String getSessionid() {
		return sessionid;
	}

}
