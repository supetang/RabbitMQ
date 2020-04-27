package cn.tangchao.exchange.fanout;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 类说明：广播模式
 *         消费者fanout--绑定多个路由键
 * @Date:2020.04.24
 * **/
public class Consumer1 {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory= new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        final Channel channel = connection.createChannel();

        //交换器的名字和类型
        channel.exchangeDeclare(FanoutProducer.EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        //获取随机的队列名字 类似与uuid
        String queueName = channel.queueDeclare().getQueue();
        //申明路由键
        String[] routekeys = {"king","mark","james"};
        for (String routeKey:routekeys){
            //信道绑定路由主键
            channel.queueBind(queueName,FanoutProducer.EXCHANGE_NAME,routeKey);
        }
        System.out.println("[" + queueName + "]Waiting for messages:");


        //TODO 创建队列消费者
        final Consumer consumerA = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received '"  + envelope.getRoutingKey() + "':'" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumerA);
    }
}
