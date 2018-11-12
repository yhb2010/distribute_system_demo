package com.chapter3message.rabbitmq.queueroute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DirectController {

	@Autowired
    private DirectSender directSender;

	@RequestMapping("/send1")
	public String send1() {
		directSender.sendOrange();
		return "send1 orange ok";
	}

	@RequestMapping("/send2")
	public String send2() {
		directSender.sendBlack();
		return "send2  black ok";
	}

	@RequestMapping("/send3")
	public String send3() {
		directSender.sendGreen();
		return "send3 green ok";
	}

}
