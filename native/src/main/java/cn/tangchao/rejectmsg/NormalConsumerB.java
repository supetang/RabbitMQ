package cn.tangchao.rejectmsg;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 类说明：正常的消费者
 */
public class NormalConsumerB {
    public static void main(String[] argv) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");

        //打开连接和创建频道，与发送端一样
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        channel.exchangeDeclare(RejectProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        //TODO 消费者一个队列
        String queueName = "rejecterror";
        channel.queueDeclare(queueName, false, false, false, null);

        //TODO 通过路由主键绑定交换器和队列
        String routekey = "error1";/*表示只关注error级别的日志消息*/
        channel.queueBind(queueName, RejectProducer.EXCHANGE_NAME, routekey);

        System.out.println("waiting for message........");

        //TODO 消费者演示
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                try {
                    String message = new String(body, "UTF-8");
                    System.out.println("Received["+ envelope.getRoutingKey() + "]" + message);
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } catch (Exception e) {
                    //TODO 这里为什么消费B需要拒接 而消费A不需要了？
                    channel.basicReject(envelope.getDeliveryTag(), false);
                }
            }
        };
        //TODO 消费者开始正式的在指定的队列上消费消息
        channel.basicConsume(queueName, false, consumer);
    }
}
