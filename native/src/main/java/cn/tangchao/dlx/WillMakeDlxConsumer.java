package cn.tangchao.dlx;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 类说明：普通的消费者,但是自己无法消费的消息，将投入死信队列
 */
public class WillMakeDlxConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        final Channel channel = connection.createChannel();
        //TODO 绑定交换器
        channel.exchangeDeclare(DlxProducer.EXCAHNGE_NAME, BuiltinExchangeType.TOPIC);

        //TODO  绑定死信交换器 声明一个队列，并绑定死信交换器
        String queueName = "dlx_make";
        Map<String, Object> map = new HashMap<>();
        //死信队列为 dlx_accept
        map.put("x-dead-letter-exchange", DlxProcessConsumer.DLX_EXCHANGE_NAME);
        channel.queueDeclare(queueName, false, true, false, map);

        //绑定 将队列和交换器通过路由主键绑定
        channel.queueBind(queueName, DlxProducer.EXCAHNGE_NAME, "#");

        System.out.println("waiting for message........");

        //TODO 消费者演示
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                //TODO 如果是king的消息就确认
                if (envelope.getRoutingKey().equals("king")) {
                    System.out.println("Received[" + envelope.getRoutingKey() + "]" + message);
                    //TODO 消息以ack确认 批量处理
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } else {
                    //TODO 如果是其他的消息就拒绝（queue=false）,第二个参数来了 很重要 成为死信消息
                    System.out.println("Will reject[" + envelope.getRoutingKey() + "]" + message);
                    channel.basicReject(envelope.getDeliveryTag(), false);
                }
            }
        };
        //TODO 消费者正式开始在指定队列上消费消息
        channel.basicConsume(queueName, false, consumer);
    }
}
