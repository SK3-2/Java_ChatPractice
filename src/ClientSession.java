import java.io.IOException;

public class ClientSession {

    private String myID;
    private int color = 0;
    private ClientSocketChannel socketChannel;

    private String cText;

    ClientSession(String id, ClientSocketChannel sockChannel){
        myID = id;
        socketChannel = sockChannel;
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
        String colorFrame = "\u001B["+ ((Integer)color).toString() +"m";
        return colorFrame;
    }


    public void send(String buffer) throws IOException{
        int n;
        int size = buffer.length();
        String string =  buffer;

        if(string.isEmpty()){
            System.out.println("Empty message is submitted.");
            return;
        }

        string += getColorFrame();

        socketChannel.send(string);
        //System.out.println("Message is partially sent.");

        return;
    }

    public void set(Message msg){
        String command = msg.getCommand();
        if(command == "color"){
            Integer color = Integer.valueOf(msg.getValue());
            setColor(color);
        }
    }
}
