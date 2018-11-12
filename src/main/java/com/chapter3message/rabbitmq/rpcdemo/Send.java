package com.chapter3message.rabbitmq.rpcdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息生产者
 *
 * @author liaokailin
 * @version $Id: Send.java, v 0.1 2015年11月01日 下午4:22:25 liaokailin Exp $
 */
@RestController
public class Send {

	@Autowired
	private Proceducer proceducer;

    @RequestMapping("send")
    //http://localhost:8080/send
    public String sendMsg() {
    	Person person = new Person("liucc", 22);
        proceducer.sendMessage(person);
        proceducer.sendMessage2(person);
        return null;
    }

}
