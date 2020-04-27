package cn.tangchao.producer_balance.producerconfirm;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 类说明：生产者——发送方确认模式--异步监听确认
 * TODO 异步模式的核心就是监听器  channel.addConfirmListener 单条 批量确认发送效应很快 但是更快的就是异步监听
 * 异步方式 ，还要快
 */
public class ProducerConfirmAsync {

    public final static String EXCHANGE_NAME = "producer_async_confirm";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        //创建连接连接到MabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        // 设置MabbitMQ所在主机ip或者主机名
        factory.setHost("127.0.0.1");
        // 创建一个连接
        Connection connection = factory.newConnection();
        // 创建一个信道
        Channel channel = connection.createChannel();
        // 指定转发
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //TODO 启用发送者确认模式
        channel.confirmSelect();
        //TODO添加发送者确认监听器
        channel.addConfirmListener(new ConfirmListener() {

            //TODO 成功 ack 成功就RabbitMQ就发送Ack deliveryTag就是RabbitMQ的唯一标识 multiple代表是批量的方式 只要超过一个数量是true
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("send_ACK:" + deliveryTag + ",multiple:" + multiple);
            }

            //TODO 失败 Nack 只有当内部发生错误的时候才能触发 所以Error很难以触发 一般是发送回ack
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("Erro----send_NACK:" + deliveryTag + ",multiple:" + multiple);
            }
        });


        //TODO 添加失败者通知
        channel.addReturnListener(new ReturnListener() {
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey,
                                     AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body);
                System.out.println("RabbitMq路由失败:  " + routingKey + "." + message);
            }
        });


        String[] routekeys = {"king", "mark"};
        //TODO 6条
        for (int i = 0; i < 20; i++) {
            String routekey = routekeys[i % 2];
            //String routekey = "king";
            // 发送的消息
            String message = "Hello World_" + (i + 1) + ("_" + System.currentTimeMillis());
            channel.basicPublish(EXCHANGE_NAME, routekey, true, MessageProperties.PERSISTENT_BASIC, message.getBytes());
        }
        // 关闭频道和连接 好奇怪 为什么要关闭
        //channel.close();
        //connection.close();
    }
}
