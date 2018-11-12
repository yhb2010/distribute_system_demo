package com.chapter8.jersey;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.springframework.stereotype.Component;

import com.chapter8.jersey.beanparam.AddParam;
import com.chapter8.jersey.beanparam.AddParam2;

@Path("/")
@Component
public class RestResource {

	//通过@Context 注释获取ServletConfig、ServletContext、HttpServletRequest、HttpServletResponse和HttpHeaders等
	@Context
    HttpServletRequest req;
    @Context
    ServletConfig servletConfig;
    @Context
    ServletContext servletContext;

	//http://localhost:8080/rest/hello/zsl?age=11
	//使用@GET标注是get请求
	@GET
	//使用@Produces指定的返回的数据类型
	@Produces(MediaType.APPLICATION_JSON)
	//@Path进行指定访问路径
	@Path("/hello/{name}")
	//@QueryParam注入?后面的查询参数值
	//@DefaultValue设置默认值
	public Map<String, Object> hello(@PathParam("name") String name, @DefaultValue("10") @QueryParam("age") int age) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", "1");
		map.put("codeMsg", "success");
		return map;
	}

	//http://localhost:8080/rest/hello2;name=zsl;surname=lisi
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/hello2")
	//@matrixparam注释可以用来绑定包含多个   property (属性)=value(值)方法参数表达式，允许你在uri中传入参数
	public OptionResult hello2(@MatrixParam("name") String name, @MatrixParam("surname") String surname) {
		OptionResult result = new OptionResult();
		result.setResult("success");
		result.setErrorMsg("no");
		return result;
	}

	//http://localhost:8080/rest/1+2/5+66
	@GET
	@Path("/{a}+{b}/{c}+{d}")
    public String add(@BeanParam AddParam param, @BeanParam AddParam2 param2){
        int c = param.getA() + param.getB();
        System.out.println(param.getWhichBrowser());
        System.out.println(param.getSessionid());
        return "<h1>The result is "+c+"</h1>";
    }

	//@FormParam可以用来注入web表单的参数为REST风格的Web服务。
	/**
	<form method="POST" action="login">
	    Email Address: <input type="text" name="email"><br>
	    Password:      <input type="text" name="password">
	    <input type="submit">
	</form>
	 * */
	@Path("/login")
	@POST
	public String login(@FormParam("email") String e, @FormParam("password") String p) {
		return "Logged with " + e + " " + p;
	}

	//http://localhost:8080/rest
	@GET
    public String get(@Context HttpHeaders hh) {
        MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
        Map<String, Cookie> pathParams = hh.getCookies();
        return null;
    }

}