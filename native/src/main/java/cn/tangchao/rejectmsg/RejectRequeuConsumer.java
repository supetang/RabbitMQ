package cn.tangchao.rejectmsg;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/***
 *消费者拒绝的流程图.png
 * TODO 类说明:拒绝消息的消费者Consumer
 *  三个消费者 A B 还有一个是拒绝的消费者
 *  三个消费者启动 再启动生产者 进行消费
 *  如果requeue设置false 消息会丢失 反之重新投递
 * */
public class RejectRequeuConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        final Channel channel = connection.createChannel();

        //TODO 绑定交换器
        channel.exchangeDeclare(RejectProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        //TODO 消费者一个队列
        String queueName = "rejecterror";
        channel.queueDeclare(queueName, false, false, false, null);

        //TODO 通过路由键绑定消息队列和交换器
        String routekey = "error1";
        channel.queueBind(queueName, RejectProducer.EXCHANGE_NAME, routekey);

        System.out.println("waiting for message........");

        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                try {
                    String message = new String(body, "UTF-8");
                    System.out.println("Received[" + envelope.getRoutingKey() + "]" + message);
                    //TODO 消息的拒绝方式 比如抛出异常
                    throw new RuntimeException("处理异常" + message);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    //TODO Reject拒绝方式1(第二个参数为是否重新投递 true消息回到RabbitMq重新发送 false消息不会发送到RabbitMq,消息就会丢失)
                    //channel.basicReject(envelope.getDeliveryTag(),true);

                    //TODO Reject拒绝方式2 Nack方式的拒绝（第2个参数决定是否批量）
                    channel.basicNack(envelope.getDeliveryTag(), false, true);
                }
            }
        };
        //TODO 消费者开始正式的在指定的队列上消费消息 拒绝autoAck=false
        channel.basicConsume(queueName, false, consumer);
    }
}
