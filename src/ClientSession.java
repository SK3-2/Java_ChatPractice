public class ClientSession {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private String myID;
    private int color = 39;
    private ClientSocketChannel socketChannel;


    ClientSession(String id, ClientSocketChannel sockChannel){
        myID = id;
        socketChannel = sockChannel;
    }

    public void setColor(int color){
        this.color = color;

    }

    public int getColor(){
        return this.color;
    }

    public String getColorFrame(){

    }





}
