package cn.tangchao.producer_balance.backupexchange;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.concurrent.TimeoutException;

/**
 * 消费主队列1 常规的消费者
 * TODO 常规设计RabbitMQ高可用的方案：失败通知 发布者的确认 备用交换器
 *                      高可用队列 + 事务 +消息的持久化
 */
public class MainConsumer {

    public static void main(String[] argv) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");

        // 打开连接和创建频道，与发送端一样
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        // 声明一个队列
        String queueName = "backupexchange";
        channel.queueDeclare(queueName, false, false, false, null);

        String routekey = "king";//只关注king级别的日志，然后记录到文件中去。
        channel.queueBind(queueName, BackupExProducer.EXCHANGE_NAME, routekey);

        System.out.println(" [*] Waiting for messages......");

        // 创建队列消费者
        final Consumer consumerB = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                //记录日志到文件：
                System.out.println("Received [" + envelope.getRoutingKey() + "] " + message);
            }
        };
        channel.basicConsume(queueName, true, consumerB);
    }

}
