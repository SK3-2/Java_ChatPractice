import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class main {
    private  static InetSocketAddress sockAddr;

    public static void main(String[] args) {
        int port_in = Integer.parseInt(args[1]);
        if (args.length == 0) {
            System.err.println("접속하실 서버를 입력해주세요.");
            System.exit(1);
        }

        ServerSocketChannel serverSocketChannel = CreateServer(port_in);

        Message Msg = new Message();

        EventManager EM = new EventManager(serverSocketChannel, Msg);

        ClientManager CM = new ClientManager(EM);

        System.out.println("티맥스 대화방 시작");

        EM.run(); //do_Poll 동치

    }

    public static ServerSocketChannel CreateServer(int port) {
        try {
            sockAddr = new InetSocketAddress("localhost", port);
            ServerSocketChannel socketChannel = ServerSocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.bind(sockAddr);
            System.out.println("티맥스 대화방 초기화 중.." + socketChannel.getLocalAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

