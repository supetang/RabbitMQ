package cn.tangchao.rejectmsg;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 类说明：普通的消费者
 */
public class NormalConsumerA {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");

        Connection connection = connectionFactory.newConnection();
        final Channel channel = connection.createChannel();

        //TODO 消费者一个队列
        String queueName = "rejecterror";
        channel.queueDeclare(queueName, false, false, false, null);

        //TODO 通过路由主键绑定交换器和队列
        String routekey = "error1";
        channel.queueBind(queueName, RejectProducer.EXCHANGE_NAME, "direct");

        System.out.println("waiting for message........");

        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received[" + envelope.getRoutingKey() + "]" + message);
                //TODO 消息的确认 一般的写法
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        //TODO 消费者开始正式的在指定的队列上消费消息
        channel.basicConsume(queueName, false, consumer);
    }
}
