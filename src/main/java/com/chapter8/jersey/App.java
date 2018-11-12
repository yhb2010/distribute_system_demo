package com.chapter8.jersey;

import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

/**编写一个App.java启动文件，在此代码中使用ServletRegistrationBean注册ServletContainer
 * @author dell
 *
 */
@SpringBootApplication
public class App {

	@Bean
    public ServletRegistrationBean jerseyServlet() {
      ServletRegistrationBean registration = new ServletRegistrationBean(new ServletContainer(), "/rest/*");
       // our rest resources will be available in the path /rest/*
       registration.addInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, JerseyConfig.class.getName());
       return registration;
    }

    public static void main(String[] args) {
       SpringApplication.run(App.class, args);
    }

}
