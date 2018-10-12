import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public abstract class ClientSocketChannel extends SocketChannel {

    ClientSocketChannel(){
        super.;
    };

    public String receive(){
        Charset charset = Charset.forName("UTF-8");
        ByteBuffer buffer = null;
        buffer = ByteBuffer.allocate(100);
        int bytecount = super.read(buffer);
        return charset.decode(buffer).toString();
    }

}
