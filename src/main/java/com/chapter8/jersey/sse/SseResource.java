package com.chapter8.jersey.sse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.springframework.stereotype.Component;

//SSE服务器推送事件
//发布——订阅模式
@Path("see-events")
@Component
public class SseResource {

	private EventOutput eventOutput = new EventOutput();
	private OutboundEvent.Builder eventBuilder;
	private OutboundEvent event;

	//提供sse事件输出通道的资源方法
	@GET
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	public EventOutput getServerSentEvents(){
		//不断循环执行
		while(true){
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//设置日期格式
			String now = df.format(new Date());
			String message = "Server Time: " + now;
			System.out.println(message);

			eventBuilder = new OutboundEvent.Builder();
			eventBuilder.id(now);
			eventBuilder.name("message");
			eventBuilder.data(String.class, message);

			event = eventBuilder.build();

			try{
				eventOutput.write(event);
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				try{
					eventOutput.close();
					return eventOutput;
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}

}
