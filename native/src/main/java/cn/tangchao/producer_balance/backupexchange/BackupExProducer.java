package cn.tangchao.producer_balance.backupexchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 类说明：生成者----绑定了一个备用交换器
 * 简单是结论:主的消费方只是消费主订阅的消息
 *           备份的消费的可以消费到主订阅消费不到的消息
 */
public class BackupExProducer {
    public final static String EXCHANGE_NAME = "main-exchange";
    public final static String BAK_EXCHANGE_NAME = "ae";

    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        // 创建一个连接
        Connection connection = connectionFactory.newConnection();
        // 创建一个信道
        final Channel channel = connection.createChannel();
        //TODO 声明备用交换器 使用hashMap容器来绑定
        Map<String, Object> argsMap = new HashMap<>();
        argsMap.put("alternate-exchange", BAK_EXCHANGE_NAME);//这一步是写死的 备用交换器的名称：ae

        //TODO 主的交换器  申明的交换器的类型为完全匹配的类型
        channel.exchangeDeclare(EXCHANGE_NAME, "direct", false, false, argsMap);//绑定是direct交换器 将备用交换器的以hashMap的方式
        //备用交换器 备用交换器的绑定的关系是FANOUT
        channel.exchangeDeclare(BAK_EXCHANGE_NAME, BuiltinExchangeType.FANOUT, true, false, null);

        String[] routekeys = {"king", "mark", "james"};
        for (int i = 0; i < 3; i++) {
            String routekey = routekeys[i % 3];
            //发送的消息
            String message = "Hello World_" + (i + 1);
            //发送的消息的动作
            channel.basicPublish(EXCHANGE_NAME, routekey, null, message.getBytes());
            System.out.println(" [x] Sent '" + routekey + "':'" + message + "'");
        }
        //关闭资源
        channel.close();
        connection.close();
    }
}
