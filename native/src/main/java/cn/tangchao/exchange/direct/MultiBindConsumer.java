package cn.tangchao.exchange.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *类说明：队列和交换器的多重绑定的案例
 *  MultiBindConsumer VS NormalConsumer
 * @Date: 2020.04.24
 */
public class MultiBindConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        //连接Rabbit工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //连接rabbitMq的地址 或则localhost
        connectionFactory.setHost("127.0.0.1");
        //打开连接和创建频道，与发送端一样
        Connection connection = connectionFactory.newConnection();
        //通过连接创建信道
        Channel channel = connection.createChannel();
        //通过信道设置 设置交换器类型
        channel.exchangeDeclare(DirectProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        //声明一个随机的队列的名字 类似uuid
        String queueName  = channel.queueDeclare().getQueue();

        //TODO 将队列绑定到交换器上，是允许绑定多个路由键的，也就是多重绑定
        //声明路由主键
        String[] routeKeys = {"king","mark","james"};
        for (String routekey : routeKeys){

            channel.queueBind(queueName,DirectProducer.EXCHANGE_NAME,routekey);
        }
        System.out.println(" [*] Waiting for messages:");


        //TODO 固定写法：创建队列消费者
        final Consumer consumerA = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" Received " + envelope.getRoutingKey() + ":'" + message + "'");
            }
        };
        //TODO 这里第二个参数是自动确认参数，如果是true则是自动确认
        channel.basicConsume(queueName, true, consumerA);
    }
}
