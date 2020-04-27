package cn.tangchao.consumer_balance.qos;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 类说明：普通的消费者
 */
public class QosConsumerMain {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        final Channel channel = connection.createChannel();
        //TODO 消费端绑定生产者的交换器
        channel.exchangeDeclare(QosProducer.EXCHANGE_NAME, "direct");

        //TODO 消费者申明的队列
        String queueName = "qos_focuserror";
        channel.queueDeclare(queueName, false, false, false, null);//消费者这里是不是固定这么写 最后一个null?

        //TODO 通过路由键来绑定交换器和队列
        String routekey = "error";//对应生产者的路由键  direct_qos
        channel.queueBind(queueName, QosProducer.EXCHANGE_NAME, routekey);

        System.out.println("waiting for message........");

        //TODO 演示
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received[" + envelope.getRoutingKey() + "]" + message);
                //TODO 单条确认 避免RabbitMq消息重复消费 以ack模式
                channel.basicAck(envelope.getDeliveryTag(), true);
            }
        };
        //TODO qos 150条预留（150都取出来，210-150=60） 如果开启批量处理就需要将qos的业务的注释
        //channel.basicQos(150, true);
        //TODO qos 消费者正式开始在指定的队列上进行消费
        //channel.basicConsume(queueName, false, consumer);


        //TODO 批量 自定义消费者批量处理 只有批量的才开启一下代码
        BatchAckConsumer batchAckConsumer = new BatchAckConsumer(channel);
        channel.basicConsume(queueName, false, batchAckConsumer);
    }
}
