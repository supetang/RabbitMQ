package cn.tangchao.producer_balance.backupexchange;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 绑定备用交换器队列的消费者
 * TODO 关于备用交换器 当设置mandatory=true的属性的时候，会发生什么，不会有失败者通知 消息有备用交换器来处理【备胎的】
 */
public class BackupExConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        final Channel channel = connection.createChannel();

        //TODO 由于交换器的类型是广播的模式 所以备用交换器的可以订阅到主交换器的订阅不到的所有的信息 即mark james
        channel.exchangeDeclare(BackupExProducer.BAK_EXCHANGE_NAME, BuiltinExchangeType.FANOUT, true, false, null);

        //申明一个队列
        String queueName = "fetchother";
        channel.queueDeclare(queueName, false, false, false, null);

        channel.queueBind(queueName, BackupExProducer.BAK_EXCHANGE_NAME, "#");

        System.out.println(" [*] Waiting for messages......");

        // 创建队列消费者
        final Consumer consumerB = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                //记录日志到文件：
                System.out.println("Received [" + envelope.getRoutingKey() + "] " + message);
            }
        };
        channel.basicConsume(queueName, true, consumerB);
    }
}
