import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class EventManager {

    Message mptr;
    private Selector select = null;

    public EventManager(ServerSocketChannel serverSocketChannel, Message ptr) {
        mptr = ptr;

    }



    @Override
    public void run() { //do_Poll 동치
        // TODO Auto-generated method stub
        try {
            // selector 열기
            select = Selector.open();
            // 서버 소켓 채널 열기
            ServerSocketChannel sockChannel = ServerSocketChannel.open();
            sockChannel.configureBlocking(false); // blocking 모드 false

            sockChannel.bind(sockAddr);
            System.out.println("Start Server:" + sockChannel.getLocalAddress());
            // 서버 셀렉터를 클라이언트 연결을 수용하기 위한 키로 등록합니다.
            sockChannel.register(select, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int count = 0;

        while (true) {
            try {
                count++;
                System.out.println(count);
                int keyCount = select.select(); // 여기서 Blocking 됨. 등록된 키를 가져온다.

                if (keyCount == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = select.selectedKeys();
                // select.selectNow(); // 비동기 처리.
                Iterator<SelectionKey> iterator = selectedKeys.iterator();

                while (iterator.hasNext()) {
                    System.out.println(count);
                    SelectionKey key = iterator.next();

                    if (!key.isValid()) { // 사용가능한 상태가 아니면 그냥 넘어감.
                        continue;
                    }

                    if (key.isAcceptable()) { // select가 accept 모드이면
                        accept(key);
                    } else if (key.isReadable()) { // select가 read 모드이면
                        System.out.println("read stage!");
                        read(key);
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
