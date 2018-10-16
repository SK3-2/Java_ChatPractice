import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ClientManager {
    private HashMap socketToIDHash = new HashMap();
    private HashMap idToClientHash = new HashMap();
    private EventManager emptr;

    ClientManager(EventManager ptr) {
        emptr = ptr;
        ptr.register_ClientManager(this);
    }

    public void registerClient(Message msg) throws IOException {
        String id = msg.getAskedID();
        SocketChannel sockChannel = msg.getFromSock();
        if(isExist(id)){
            String msgAns = "no";
            Charset charset = Charset.forName("UTF-8");
            ByteBuffer buffer = null;
            try {
                buffer = charset.encode(msgAns);
                sockChannel.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sockChannel.close(); //close and detach from the selector
            System.out.println("Submitted ID is denied. - already existed");
        } else {
            String msgAns = "yes";
            ClientSession clientSession = new ClientSession(id, sockChannel);
            System.out.println("register stage! ID: "+id);
            clientSession.send(msgAns);
            socketToIDHash.put((SocketChannel)sockChannel, (String)id);
            idToClientHash.put(id, clientSession);
            broadcastMsg(msg);
        }
    }

    private boolean isExist(String id) {
        if(idToClientHash.containsKey(id))
            return true;
        return false;
    }

    public void handler(Message msg) throws IOException {
        if (msg.isSetID()) {
            registerClient(msg);
        } else if(msg.isWhisper()){
            whisperMsg(msg);
        } else if (msg.isSetting()) {
            settingMsg(msg);
        } else if (msg.isEmpty()) {
            closeSession(msg);
        } else {
            broadcastMsg(msg);
        }
    }

    private void broadcastMsg(Message msg) throws IOException {
        SocketChannel fromSock = msg.getFromSock();
        String fromID = (String) socketToIDHash.get(fromSock);
        ClientSession fromClient = (ClientSession)idToClientHash.get(fromID);

        Set idSet = idToClientHash.entrySet();
        Iterator it = idSet.iterator();

        while(it.hasNext()){
            Map.Entry idEntry = (Map.Entry)it.next();
            if(idEntry.getKey()==fromID){
                continue;
            }
            ClientSession toClient = (ClientSession) idEntry.getValue();
            toClient.send(msg.get_MsgFrame(fromClient));
        }
    }

    private void whisperMsg(Message msg){
        try {
            SocketChannel fromSock = msg.getFromSock();
            String fromID = (String) socketToIDHash.get(fromSock);
            ClientSession fromClient = (ClientSession)idToClientHash.get(fromID);
            String toID = msg.getToID();

            if(!idToClientHash.containsKey(toID)){
                //Send back to the sender
                fromClient.send("Fail to find the user with that name");
            } else{
                //Find pointed client
                ClientSession toClient = (ClientSession)idToClientHash.get(toID);
                toClient.send(msg.get_MsgFrame(fromClient));
            }
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("message type exception");
            return;
        }
    }

    private void settingMsg(Message msg){
        SocketChannel fromSock = msg.getFromSock();
        String fromID = (String) socketToIDHash.get(fromSock);
        ClientSession fromClient = (ClientSession)idToClientHash.get(fromID);
        fromClient.set(msg);
    }

    public void closeSession(Message msg) {
        SocketChannel fromSock = msg.getFromSock();
        String fromID = (String) socketToIDHash.get(fromSock);
        try {
            broadcastMsg(msg);
//            fromSock.close();
        } catch (IOException e) {
            System.out.println("error");
        }
        idToClientHash.remove(fromID);
        socketToIDHash.remove(fromSock);
        try {
            fromSock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // call close() of EventManager
    }

}
