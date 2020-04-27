package cn.tangchao.exchange.direct;


import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 类说明：direct(完全匹配交换器)的生长者
 * @Date: 2020.04.24
 * */
public class DirectProducer {
    //生命exchange【交换器】的名称
    public final static String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接、连接到RabbitMQ
        ConnectionFactory connectionFactory= new ConnectionFactory();
        //设置下连接工厂的连接地址(使用默认端口5672)
        connectionFactory.setHost("localhost");
        //connectionFactory.setUsername("guest");
        //connectionFactory.setPassword("guest");
        //创建连接
        Connection connection =connectionFactory.newConnection();
        //创建信道
        Channel channel =connection.createChannel();
        //在信道中设置交换器 交换器的名称以及是哪一种类型
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        //申明路由键数组 存放消息体  其中路由主键的长度不能超过255字节
        String[] routeKeys = {"king", "mark", "james"};
        for (int i = 0; i < 3 ; i++) {
            String routeKey = routeKeys[i % 3];//具体的消息的主键
            String message = "Hello,RabbitMQ" + (i+1);//发布的消息

            //通过信道发送消息   绑定交换器   主键     ？？    消息字节
            channel.basicPublish(EXCHANGE_NAME,routeKey,null,message.getBytes());
            System.out.println("Sent:" + routeKey + ":" +message);
        }

        //TODO 关闭资源 原则从最近的开始关闭
        channel.close();
        connection.close();
    }
}
