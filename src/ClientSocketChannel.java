import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;

public abstract class ClientSocketChannel extends SocketChannel {

    ClientSocketChannel(SelectorProvider provider){
        super(provider);
    };
    Charset charset = Charset.forName("UTF-8");
    ByteBuffer buffer = null;

    public String receive(){
        try {
            buffer = ByteBuffer.allocate(1024);
            int bytecount = read(buffer);
            buffer.flip();
            return charset.decode(buffer).toString();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
    public void send(String message) {
        try {
            buffer = charset.encode(message);
            write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
