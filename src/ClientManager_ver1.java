import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ClientManager_ver1 {
    private HashMap socketIDHash = new HashMap();
    private HashMap idClientHash = new HashMap();


    public void registerClient(String id, SocketChannel sockChannel){

        if(!isExist(id)){
            String msgAns = "no";
            sockChannel.write(msgAns);
            sockChannel.close(); //close and detach from the selector
            System.out.println("Submitted ID is denied. - already existed");
        } else {
            String  msgAns = "yes";
            sockChannel.write(msgAns);

            ClientSession clientSession = new ClientSession(id, sockChannel);
            socketIDHash.put(sockChannel, id);
            idClientHash.put(id, clientSession);

            //register CS to the EventManager
            broadcastMsg("["+ id + "] enters to the Chat.");
            }
        }

    private boolean isExist(String id) {
        if(idClientHash.containsKey(id))
            return true;
        return false;
    }

    public void broadcastMsg(MsgCarrier msg) {
        Set clientSet = clientIDHash.entrySet();
        Iterator it = clientSet.iterator();

        while(it.hasNext()){
            Map.Entry clientEntry = (Map.Entry)it.next();

            ClientSession client = (ClientSession)clientEntry.getValue();
            if(client.socket == msg.socket) {
                //client.send()
            }
        }
    }

    public void whisperMsg(MsgCarrier msg){
        try {
            String destID = msg.getDestID();
            SocketChannel fromSock = msg.getFromSock();
            SocketChannel toSock = msg.getToSock();
        } catch (){
            System.out.println("Message type exception");
        }

        if(!clientIDHash.containsKey(destID)){
            //send ( fromSock.  "Fail to find the user with that name"          )
        } else{
            ClientSession client = (ClientSession)clientIDHash.get(destID);
            //client.sendMsg(msg.~~)
            //send ( toSock.
        }
    }

    public void settingMsg(MsgCarrier msg){
        try{
            SocketChannel fromSock = msg.getFromSock();


            Set.clientSet
        }
    }

    public void closeSession(ClientSession clientSession){
        clientIDHash.remove(id);
        clientSockHash.remove()

    }

}
