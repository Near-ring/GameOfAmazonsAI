package amazon_ai.remote_cpp_agent;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


/**
 * Copyright: This class is modified from an original article by CSDN blogger "神仙来了呀“,
 * following the CC 4.0 BY-SA copyright agreement, please include the original source link and this statement.
 * Link to original article: https://blog.csdn.net/qq_43646059/article/details/116307127
 *
 * 版权声明：本类修改自CSDN博主「神仙来了呀」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
 * 原文链接：https://blog.csdn.net/qq_43646059/article/details/116307127
 */

public class JavaClient {

    public byte[] cppInterface(byte[] a) {
        byte[] ret = new byte[7];
        String mes = new String(a, StandardCharsets.UTF_8);
        try {
            Client client = ClientFactory.createClient();
            client.send(String.format(mes, client.client.getLocalPort()));
            ret = client.receive();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    static class ClientFactory {

        public static Client createClient() throws Exception {
            return new Client("127.0.0.1", 15333);
        }
    }

    /**
     * 客户端
     */
    static class Client {

        /**
         * 构造函数
         * @param host 要连接的服务端IP地址
         * @param port 要连接的服务端对应的监听端口
         * @throws Exception
         */
        public Client(String host, int port) throws Exception {
            // 与服务端建立连接
            this.client = new Socket(host, port);
            // this.client = new Socket();
            // this.client.connect(new InetSocketAddress(InetAddress.getLocalHost(), port),30000);
            System.out.println("Cliect[port:" + client.getLocalPort() + "] Establishing connection with C++ server ...");
        }

        private Socket client;

        private Writer writer;

        /**
         * 发送消息
         * @param msg
         * @throws Exception
         */
        public void send(String msg) throws Exception {
            // 建立连接后就可以往服务端写数据了
            if(writer == null) {
                writer = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
            }
            writer.write(msg);
            // 写完后要记得flush
            writer.flush();
            System.out.println("Cliect[port:" + client.getLocalPort() + "] Action sent");
        }

        /**
         * 接收消息
         * @throws Exception
         */
        public byte[] receive() throws Exception {
            // 写完以后进行读操作
            Reader reader = new InputStreamReader(client.getInputStream(), "UTF-8");
            // 设置接收数据超时间为60秒
            client.setSoTimeout(60*1000);
            char[] chars = new char[64];
            byte[] data = new byte[64];
            int i;
            StringBuilder sb = new StringBuilder();
            while ((i = reader.read(chars)) != -1) {
                //sb.append(new String(chars, 0, i));
            }
            for (int j=0; j<7; j++)
            {
                data[j] = (byte)chars[j];
            }
            //System.out.println("Cliect[port:" + client.getLocalPort() + "] 消息收到了，内容:" + sb.toString());
            reader.close();

            // 关闭连接
            writer.close();
            client.close();
            return data;
        }
    }
}