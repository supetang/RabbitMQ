package cn.tangchao.exchange.fanout;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 类说明：广播生产者：
 * */
public class FanoutProducer {

    public final static String EXCHANGE_NAME = "fanout_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        //交换器 设置名称和类型 广播模式
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        //申明路由主键
        String[] routeKeys = {"king","mark","james"};
        for (int i = 0; i < 3; i++) {
            String routeKey = routeKeys[i % 3]; //获取到路由主键
            String message = "Hello Rabbit" + (i+1);//发送的消息
           //TODO 生成者发布消息
            //信道发送绑定路由键消息
            channel.basicPublish(EXCHANGE_NAME,routeKey,null,message.getBytes());
            //TODO 这里就是打印输出  [x1] Sentking:Hello Rabbit1
            System.out.println(" [x] Sent '" + routeKey +"':'" + message + "'");
        }

        // 关闭频道和连接
        channel.close();
        connection.close();
    }
}
