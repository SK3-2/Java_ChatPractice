import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

import static java.lang.System.exit;

public class Client {
    public static Charset charset = Charset.forName("UTF-8");
    SocketChannel sock;

    public static void main(String[] args) throws UnknownHostException {
        if (args.length <3) {
            System.out.printf("Usage: %s [SERVER_ADDRESS] [TCP_PORT] [My_ID]\n",args[0]);
            exit(1);
        }

        InetAddress host = Inet4Address.getByName(args[0]);
        int port = Integer.valueOf(args[1]);
        String ID = args[2];

        Scanner scan = new Scanner(System.in);

        Client client = new Client();
        ByteBuffer byteBuffer1;
        byteBuffer1 = ByteBuffer.allocate(1024);

        while (true) {
            try {
                System.out.println("host: " + host);
                System.out.println("port: "+ port);
                client.connect(host,port);
                ByteBuffer byteBuffer = null;

                byteBuffer = charset.encode("/id " + ID);
                client.sock.write(byteBuffer);


                client.sock.read(byteBuffer1);
                byteBuffer1.flip();
                String ret = charset.decode(byteBuffer1).toString();
                System.out.println(ret);

                if (ret.length()>=3 && (ret.substring(0, 3).compareTo("yes") == 0)) {
                    System.out.println("ID check Success.");
                    break;
                }
                System.out.println("이미 존재하는 ID 입니다. 다시 시도하십시오... ");
                ID = scan.nextLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }
        }
            client.read();
            client.chatStart();
        }


    public void connect(InetAddress host, int port) throws UnknownHostException, IOException {
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
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        int byteCount = sock.read(byteBuffer);
                        byteBuffer.flip();
                        String message = charset.decode(byteBuffer).toString();
                        if (message.isEmpty()) {
                            System.out.println("서버가 종료 되었습니다.");
                            exit(0);
                        }
                        System.out.println(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }

        };
        thread.start();
    }

    public void chatStart() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            String message = scan.nextLine();
            //System.out.println(message);
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

