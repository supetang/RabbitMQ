package cn.tangchao.dlx;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 类说明：这是普通的消费者 只是消费当前死信队列
 */
public class DlxProcessConsumer {

    public final static String DLX_EXCHANGE_NAME = "dlx_accept";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        final Channel channel = connection.createChannel();

        //TODO 申明交换器的类型
        channel.exchangeDeclare(DLX_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        //TODO 申明队列
        String queueName = "dlx_accept";
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, DLX_EXCHANGE_NAME, "#");

        System.out.println("waiting for message........");

        //TODO 死信队列消费演示
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received dead letter[" + envelope.getRoutingKey() + "]" + message);
            }
        };

        //TODO 消费者正式开始在指定队列上消费消息
        channel.basicConsume(queueName,true,consumer);
    }
}
