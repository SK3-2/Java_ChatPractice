import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class ClientSession {

    private String myID;
    private int color = 0;
    private SocketChannel socketChannel;

    private String cText;

    ClientSession(String id, SocketChannel sockChannel){
        myID = id;
        this.socketChannel = sockChannel;
    }

    public String getMyID(){
        return myID;
    }

    public void setColor(int color){
        this.color = color;
    }

    public int getColor(){
        return this.color;
    }

    public String getColorFrame(){
        String colorFrame = "\33["+ ((Integer)color).toString() +"m";
        return colorFrame;
    }


    public void send(String buf) throws IOException{
        int n;
        int size = buf.length();
        String string =  buf;

        if(string.isEmpty()){
            System.out.println("Empty message is submitted.");
            return;
        }
        string += getColorFrame();
        send(string, this.socketChannel);
        return;
    }

    public void send(String message, SocketChannel channel) {
        Charset charset = Charset.forName("UTF-8");
        System.out.println("message: "+message);

        try {
            ByteBuffer buffer = null;
            buffer = charset.encode(message);
            channel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void set(Message msg) throws IOException {
        String command = msg.getCommand();
        if(command.equals("color")){
            int color = Integer.valueOf(msg.getValue());
            setColor(color);
            send("User color is successfully changed.");
        }
    }
}
