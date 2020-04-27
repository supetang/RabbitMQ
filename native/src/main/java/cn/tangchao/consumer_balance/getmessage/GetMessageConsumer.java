package cn.tangchao.consumer_balance.getmessage;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 类说明：拉区模式----消费者端
 * TODO 解决RabbitMq消息重复：使用到ack的确认的机制，将消费的队列/消息->消费 那么将确认以ack的机制发送RabbitMq,然后RabbitMq将消息给处理掉。就消费者有重复消费的消息
 *
 *         消费者在进行消息消费时候,无论是拉取还是推送，都需要进行消息的确认
 */
public class GetMessageConsumer {


    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        final Channel channel = connection.createChannel();

        channel.exchangeDeclare(GetMessageProducer.EXCHANGE_NAME, "direct");//交换器名字以及是那种类型
        //申明队列的名字
        String queueName = "focuserror";
        channel.queueDeclare(queueName, false, false, false, null);

        //申明的路由主键
        String routekey = "error";
        channel.queueBind(queueName, GetMessageProducer.EXCHANGE_NAME, routekey);

        System.out.println(" [*] Waiting for messages......");

        //TODO 无限循环拉取
        while (true) {
            // TODO 39行true为自动确认 不需要44行代码  如果是手动确认 false
            // TODO 这里是false 手动确认 就需要有44code 如果是true就不需要 注释444code (RabbitMQ就会认为这条消息消费 --从RabbitMQ删除)
            GetResponse getResponse = channel.basicGet(queueName, false);
            if (null != getResponse) {
                System.out.println("Received [" + getResponse.getEnvelope().getRoutingKey() + "] " + new String(getResponse.getBody(), "UTF-8"));
            }
            //消息确认
            channel.basicAck(0, true); //这里是写死 具体有申明更加优雅的操作的看情况
            Thread.sleep(1000);//1秒这里又为啥需要休眠？为了增加每次循环的时间，减少去RabbitMQ去拉取消息的次数，就能有效的减少开销
        }

        //TODO 验证消息删除 重启的消费者 如果是false 注释43行代码 队列中就会有重复的消息 结论必须要的走ack的模式解决消息重复
    }
}
