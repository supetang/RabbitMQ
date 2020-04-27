package cn.tangchao.dlx;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 类说明：死信案例 信号生产者
 */
public class DlxProducer {

    public final static String EXCAHNGE_NAME = "dlx_make";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        final Channel channel = connection.createChannel();

        //TODO 声明交换器的类型
        channel.exchangeDeclare(EXCAHNGE_NAME, BuiltinExchangeType.TOPIC);

        String[] rotekeys = {"king", "mark", "james"};
        for (int i = 0; i < 3; i++) {
            String routekey = rotekeys[i % 3];
            String message = "dlx Hello,RabbitMq" + (i + 1);
            //TODO 发布消息
            channel.basicPublish(EXCAHNGE_NAME, routekey, null, message.getBytes());
            System.out.println("Sent " + routekey + ":" + message);
        }
        //TODO 关闭资源
        channel.close();
        connection.close();
    }
}
