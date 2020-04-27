package cn.tangchao.rejectmsg;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 类说明:这是一个拒绝模式生产者
 * 消费者拒绝.png
 */
public class RejectProducer {

    public final static String EXCHANGE_NAME = "reject_direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        final Channel channel = connection.createChannel();

        //TODO 交换器的绑定
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        //TODO 发送消息 路由主键error1
        for (int i = 0; i < 10; i++) {
            String message = "Hello World_" + (i + 1);
            channel.basicPublish(EXCHANGE_NAME, "error1", null, message.getBytes());
            System.out.println(" [x] Sent 'error':'" + message + "'");
        }
        //TODO 关闭资源
        channel.close();
        connection.close();
    }
}
