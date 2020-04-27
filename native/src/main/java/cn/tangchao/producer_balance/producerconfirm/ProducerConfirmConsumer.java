package cn.tangchao.producer_balance.producerconfirm;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 类说明：消费者——发送方确认模式
 */
public class ProducerConfirmConsumer {

    public static void main(String[] argv) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        // 打开连接和创建频道，与发送端一样
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        //TODO 这里的交换器和生成者是一一对应的 修改是便于验证ProducerBatchConfirm
        channel.exchangeDeclare(ProducerConfirmAsync.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        String queueName = ProducerConfirmAsync.EXCHANGE_NAME;
        channel.queueDeclare(queueName, false, false, false, null);

        //只关注king  多个消息  只是消费king
        String routekey = "king";
        channel.queueBind(queueName, ProducerConfirmAsync.EXCHANGE_NAME, routekey);

        System.out.println(" [*] Waiting for messages......");

        // 创建队列消费者
        final Consumer consumerB = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                //记录日志到文件：
                System.out.println("Received [" + envelope.getRoutingKey() + "] " + message);
            }
        };
        channel.basicConsume(queueName, true, consumerB);
    }
}
