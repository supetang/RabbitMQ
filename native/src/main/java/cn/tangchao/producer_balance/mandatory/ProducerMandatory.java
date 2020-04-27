package cn.tangchao.producer_balance.mandatory;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * 类说明：生成者发布消失失败--失败确定模式
 * */
public class ProducerMandatory {

    public final static String EXCHANGE_NAME = "mandatory_test";

    public static void main(String[] args) throws Exception{

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        //指定是DIRECT 交换器
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        //TODO 回调处理 连接时关闭是才执行  connection
        connection.addShutdownListener(new ShutdownListener(){
            @Override
            public void shutdownCompleted(ShutdownSignalException cause) {
                //todo something
            }
        });

        //TODO 回调  信道关闭时执行   channel
         channel.addShutdownListener(new ShutdownListener() {
            public void shutdownCompleted(ShutdownSignalException e) {

            }
        });
        //TODO！！！！ 回调 失败通知
         channel.addReturnListener(new ReturnListener(){
             @Override
             public void handleReturn(int replycode, String replyText, String exchange, String routeKey, AMQP.BasicProperties basicProperties, byte[] body) throws IOException {
                 String message = new String(body,"UTF-8");
                 //消息的打印
                 System.out.println("返回的replycode:"+replycode);
                 System.out.println("返回的replyText:"+replyText);
                 System.out.println("返回的exchange:"+exchange);
                 System.out.println("返回的routeKey:"+routeKey);
             }
         });

        String[] routekeys={"king","mark","james"};
        for(int i=0;i<3;i++){
            String routekey = routekeys[i%3];
            // 发送的消息
            String message = "Hello World_"+(i+1) + ("_"+System.currentTimeMillis());
            //TODO
            channel.basicPublish(EXCHANGE_NAME,routekey,true,null,message.getBytes());
            System.out.println("----------------------------------");
            System.out.println(" Sent Message: [" + routekey +"]:'"+ message + "'");
            Thread.sleep(200);
        }
        // 关闭频道和连接
        channel.close();
        connection.close();
    }
}
