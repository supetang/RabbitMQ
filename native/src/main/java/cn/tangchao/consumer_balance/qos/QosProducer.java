package cn.tangchao.consumer_balance.qos;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 类说明：发送消息（发送210条消息，其中第210条消息表示本批次消息的结束）
 */
public class QosProducer {

    public final static String EXCHANGE_NAME = "direct_qos";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        final Channel channel = connection.createChannel();
        //TODO 绑定交换器 exchangeDeclare(String exchange, String type) 交换器的名字+交换器类型
        //channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        //TODO 申明其他类型的交换器
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        //TODO 生产200条消息
        for (int i = 0; i < 210; i++) {
            String message = "Hello supetang_" + (i + 1);
            //TODO 这是最后一条
            if (i == 209) {
                message = "stop";
            }
            //TODO 参数1：exchange name 参数2：routing key
            channel.basicPublish(EXCHANGE_NAME, "error", null, message.getBytes());
            System.out.println(" [x] Sent 'error':'" + message + "'");
        }
        //TODO 关闭连接资源
        channel.close();
        connection.close();
    }
}
