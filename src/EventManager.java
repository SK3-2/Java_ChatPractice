import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class EventManager {
    private Message mptr;
    private ClientManager cmptr;
    private Selector select = null;

    public EventManager(ServerSocketChannel serverSocketChannel, Message ptr) {
        try {
            mptr = ptr;
            select = Selector.open();
            serverSocketChannel.register(select, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            //log.info("fail to register selector");
        }
    }

    public int register_ClientManager(ClientManager ptr) {
        if (ptr == null) {
            return -1;
        }
        cmptr = ptr;
        return 0;
    }

    private void accept(SelectionKey key) {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel;

        try {
            channel = serverChannel.accept();
            channel.configureBlocking(false);
            SocketAddress remoteAddr = channel.getRemoteAddress();
            System.out.println("[Accept Socket Event Occur!]: "+ remoteAddr.toString());

            channel.register(key.selector(), SelectionKey.OP_READ | SelectionKey.OP_WRITE); // 읽을 수 있는 모드로 전환

        } catch (Exception e) {
            // TODO: handle exception
            //log.info("fail to accept socket");
        }
    }


    private String recvMsg(SelectionKey key) throws IOException {
        SocketChannel chnn = (SocketChannel) key.channel();
        String msg = receive(chnn);
        System.out.println("read Data:" + msg);
//        Charset charset = Charset.forName("UTF-8");
//        ByteBuffer buffer = null;
//        buffer = charset.encode("yes");
//        buffer.flip();
//        chnn.write(buffer);

        mptr.set_Msg(msg, chnn);
        cmptr.handler(mptr);
        return msg;
    }


    public String receive(SocketChannel channel){
        try {
            Charset charset = Charset.forName("UTF-8");
            ByteBuffer buffer = null;
            buffer = ByteBuffer.allocate(1024);
            int bytecount = channel.read(buffer);
            buffer.flip();
            return charset.decode(buffer).toString();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }


    public void closeKey(SocketChannel channel) {
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() { //do_Poll 동치
        // TODO Auto-generated method stub

        while (true) {
            try {
                int keyCount = select.select(); // 여기서 Blocking 됨. 등록된 키를 가져온다.

                if (keyCount == 0) {
                    continue;
                }
                Set<SelectionKey> selectedKeys = select.selectedKeys();
                // select.selectNow(); // 비동기 처리.
                Iterator<SelectionKey> iterator = selectedKeys.iterator();

                while (iterator.hasNext()) {

                    SelectionKey key = iterator.next();

                    if (!key.isValid()) { // 사용가능한 상태가 아니면 그냥 넘어감.
                        continue;
                    }
                    if (key.isAcceptable()) { // select가 accept 모드이면
                        accept(key);
                    } else if (key.isReadable()) { // select가 read 모드이면
                        System.out.println("read stage!");
                        recvMsg(key);
                    }
                    iterator.remove();   // 중요함 . 처리한 키는 제거
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
}
