package cn.tangchao.exchange.fanout;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 类说明：fanout消费者绑定一个不存在的路由键的案例
 * @Date:2020.04.24
 * @结论：即使是不存在的路由键 但是是还是可以订阅到所有的消息 就是路由键没有什么影响的 消息一来所有的路由都能一一接收到
 * */
public class Consumer2 {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        final Channel channel = connection.createChannel();
        //绑定交换器的名字和类型
        channel.exchangeDeclare(FanoutProducer.EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        //声明一个随机的队列名字 类似uuid
        String queueName = channel.queueDeclare().getQueue();

        //申明一个不存在路由键
        String routeKey = "supetang";
        //根信道进行绑带
        channel.queueBind(queueName,FanoutProducer.EXCHANGE_NAME,routeKey);

        System.out.println(" [*] Waiting for message ......");

        //TODO 固定写法 模拟消费队列
        final Consumer consumerB = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body,"UTF-8");
                //TODO 模拟日志记录
                System.out.println("Received [" + envelope.getRoutingKey() +"] " + message);
            }
        };
        //TODO 正在消费 消费者正在开始从指定队列上消费。(queue-king)
        channel.basicConsume(queueName,true,consumerB);
    }
}
