package cn.tangchao.exchange.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 类说明：普通的消费者如何消费交换器direct消息的案例
 * @Date : 2020.4.20
 * */
public class NormalConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建Rabbit连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //设置下连接工厂的连接地址(使用默认端口5672)
        connectionFactory.setHost("localhost");
        //创建连接
        Connection connection = connectionFactory.newConnection();
        //通过连接创建信道
        Channel channel = connection.createChannel();

        //step1
        //在信道中设置交换器 完全匹配生者生产的交换器的名字以及交换器的l类型
        channel.exchangeDeclare(DirectProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        //step2:申明队列（放在消费者中去做）
        String queueName = "queue-king";
        // 参数解释：        队列名字     是否持久化    是否排外   noWait:是否等待服务器返回 args：相关参数，目前一般为nil
        channel.queueDeclare(queueName, false, false, false, null);

        //step3绑定：将队列(queue-king)与交换器通过 路由键 绑定（king）
        String routeKey = "king";
        channel.queueBind(queueName,DirectProducer.EXCHANGE_NAME,routeKey);
        System.out.println("waiting for message ......");

        //step4 申明一个消费者
        final Consumer consumer  = new DefaultConsumer(channel){

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //从byte[]字节数组中获取到消息 类型转换
                String message = new String(body,"UTF-8");
                //输出打印
                System.out.println("Received【" + envelope.getRoutingKey() + "】" + message);
            }
        };
        //消费者正在开始从指定队列上消费。(queue-king)
        //TODO 这里第二个参数是自动确认参数，如果是true则是自动确认
        channel.basicConsume(queueName,true,consumer);
    }
}
