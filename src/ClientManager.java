import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ClientManager {
    private HashMap socketIDHash = new HashMap();
    private HashMap idClientHash = new HashMap();


    public void registerClient(String id, ClientSocketChannel sockChannel) throws IOException {
        if(!isExist(id)){
            String msgAns = "no";
            sockChannel.send(msgAns);
            sockChannel.close(); //close and detach from the selector
            System.out.println("Submitted ID is denied. - already existed");
        } else {
            String msgAns = "yes";
            sockChannel.send(msgAns);
            ClientSession clientSession = new ClientSession(id, sockChannel);
            socketIDHash.put(sockChannel, id);
            idClientHash.put(id, clientSession);

            //register CS to the EventManager
            broadcastMsg();
            }
        }

    private boolean isExist(String id) {
        if(idClientHash.containsKey(id))
            return true;
        return false;
    }

    public void handler(Message msg){
        if(msg.isWhisper()){
            whisperMsg(msg);
        } else if (msg.isSetting()){
            settingMsg(msg);
        } else {
            broadcastMsg(msg);
        }
    }

    private void broadcastMsg(Message msg) {
        SocketChannel fromSock = msg.getFromSock();
        String fromID = (String) socketIDHash.get(fromSock);
        Set socketSet = socketIDHash.entrySet();
        Iterator it = socketSet.iterator();

        while(it.hasNext()){
            Map.Entry socketEntry = (Map.Entry)it.next();
            if(socketEntry.getKey()== fromSock){
                continue;
            }
            ClientSession client = (ClientSession) idClientHash.get((String)socketEntry.getValue());
            client.send(msg.frame(fromID));
        }
    }

    private void whisperMsg(Message msg){
        try {
            SocketChannel fromSock = msg.getFromSock();
            String fromID = (String) socketIDHash.get(fromSock);
            String destID = msg.getDestID();

            if(!idClientHash.containsKey(destID)){
                //Send back to the sender
                ClientSession client_sent = (ClientSession)idClientHash.get(fromID);
                client_sent.send("Fail to find the user with that name");
                return;
            } else{
                //Find pointed client
                ClientSession client = (ClientSession)idClientHash.get(destID);
                client.send(msg.frame(fromID));
            }

        } catch (IOException e){
            e.printStackTrace();
            System.out.println("message type exception");
            return;
        }
    }

    private void settingMsg(Message msg){
        SocketChannel fromSock = msg.getFromSock();
        String fromID = (String) socketIDHash.get(fromSock);
        ClientSession client_self = (ClientSession)idClientHash.get(fromID);
        client_self.set(msg);
    }

    public void closeSession(Message msg){
        SocketChannel fromSock = msg.getFromSock();
        String fromID = (String) socketIDHash.get(fromSock);
        idClientHash.remove(fromID);
        socketIDHash.remove(fromSock);

        // call close() of EventManager
    }

}
