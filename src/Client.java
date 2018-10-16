
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

import static java.lang.System.exit;

public class Client {

    SocketChannel sock;
    String host;
    int port;

    public static void main(String[] args) {
        if (args.length <3) {
            System.out.printf("Usage: %s [SERVER_ADDRESS] [TCP_PORT] [My_ID]\n",args[0]);
            exit(1);
        }

        Scanner scan = new Scanner(System.in);
        String ID = args[2];

        Client client = new Client();

        while (true) {
            try {
                client.connect();
                ByteBuffer byteBuffer = null;
                Charset charset = Charset.forName("UTF-8");
                byteBuffer = charset.encode("/id " + ID);
                client.sock.write(byteBuffer);

                ByteBuffer byteBuffer1;
                byteBuffer1 = ByteBuffer.allocate(1024);
                client.sock.read(byteBuffer1);
                byteBuffer1.flip();

                String ret = charset.decode(byteBuffer1).toString();
                System.out.println(ret);

                if (ret.substring(0, 3).compareTo("yes") == 0) {
                    System.out.println("ID check Success.");
                    break;
                } else {
                    System.out.println("이미 존재하는 ID 입니다. 다시 시도하십시오... ");
                    ID = scan.nextLine();
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("connect exception");
            }
        }
            client.read();
            client.chatStart();
        }



    public void connect() throws UnknownHostException, IOException {
        host = "localhost";
        port = 7001;
        sock = SocketChannel.open();
        sock.connect(new InetSocketAddress(host, port));
    }

    public void read() {

        Thread thread = new Thread() {

            @Override

            public void run() {

                // TODO Auto-generated method stub

                while (true) {
                    try {
                        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
                        int byteCount = sock.read(byteBuffer);
                        Charset charset = Charset.forName("UTF-8");
                        String message = charset.decode(byteBuffer).toString();
                        System.out.println("[데이터 받기 성공]: " + message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // TODO Auto-generated catch block


            }

        };
        thread.start();

    }

    public void chatStart() {

        Scanner scan = new Scanner(System.in);

        while (true) {

            String message = scan.nextLine();

            System.out.println(message);
            try {
                ByteBuffer byteBuffer = null;
                Charset charset = Charset.forName("UTF-8");
                byteBuffer = charset.encode(message);
                sock.write(byteBuffer);

            } catch (IOException e) {

                // TODO Auto-generated catch block

                System.out.println("client write exception!");

                try {

                    sock.close();
                    break;

                } catch (IOException e1) {

                    // TODO Auto-generated catch block

                    e1.printStackTrace();

                }

                e.printStackTrace();

            }

        }

    }

}

