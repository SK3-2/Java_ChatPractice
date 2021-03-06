import java.io.IOException;
import java.nio.channels.SocketChannel;

public class Message {
        public enum MsgType {
                GREET, BYE, WHISP, BROAD, SET
        }

        private SocketChannel channel;
        private String msgBuffer;
        private String fromID;
        private MsgType mtype;


        //Put recv Msg&Info  -- used in Receiving Object
        void set_Msg(String buf, SocketChannel channel) {
                System.out.println("setMsg: " + buf);
                msgBuffer = buf;
                mtype = parseMsg();
                this.channel = channel;
        }

        MsgType parseMsg() {
                if (msgBuffer.isEmpty()) {
                        System.out.println("Msg is empty");
                        mtype = MsgType.BYE;
                }
                else if (msgBuffer.substring(0,1).compareTo("/") == 0) {
                        if (msgBuffer.substring(1,3).compareTo("id") == 0)
                                mtype = MsgType.GREET;
                        else
                                mtype = MsgType.SET;  //SET인 경우는, Command를 추가로 비교
                }
                else if (msgBuffer.substring(0,1).compareTo("@") == 0) {
                        mtype = MsgType.WHISP;
                }
                else
                        mtype = MsgType.BROAD;
                return mtype;
        }

        //Ask by isCase
        boolean isSetID()
        {
                if (mtype == MsgType.GREET)
                        return true;
                return false;
        }

        boolean isWhisper()
        {
                if (mtype == MsgType.WHISP)
                        return true;
                return false;
        }

        boolean isSetting()
        {
                if (mtype == MsgType.SET)
                        return true;
                return false;
        }

        boolean isEmpty()
        {
                if (mtype == MsgType.BYE)
                        return true;
                return false;
        }

        //Throw Token
        String getToID() {
                return tokenMsg(msgBuffer, 1);
        }

        String getAskedID() {
                fromID = tokenMsg(msgBuffer, 2);
                return fromID;
        }

        SocketChannel getFromSock() {
            return channel;
        }

        String getCommand() {
                return tokenMsg(msgBuffer, 1);
        }

        String getValue() {
                //System.out.println("GET VALUE CALL");
                return tokenMsg(msgBuffer, 2);
        }

        //Tokenize Function
        String tokenMsg(String buf, int order) {
                int cur = 0; //Search Starts from Index 0
                int head = 0; //to erase a command character.
                if (buf.isEmpty()) {
                        return "";
                }

                for (int i = 0; i < order; i++) {
                        head = cur;
                        cur = buf.indexOf(" ", cur+1);
                        if (cur == -1) {
                                // 방어코드가 필요함, 원하는 token 개수 만큼 안 들어왔을 때.
                                return buf.substring(head+1);
                        }
                }
                return buf.substring(head + 1, cur - head);
        }

        //Revise the Msg to ID contained Format
        String get_MsgFrame(ClientSession csptr) {
                fromID = csptr.getMyID();
                String msgFrame;
                if (mtype == MsgType.GREET) {
                        msgFrame = "\33[39m[" + fromID + "] enters to the Chat.";

                } else if (mtype == MsgType.BYE) {
                        msgFrame = "\33[39m[" + fromID + "] exits from the Chat.";
                } else if (mtype == MsgType.WHISP) {
                        int cur = msgBuffer.indexOf(" ");
                        System.out.println("cur: "+cur);
                        msgFrame = csptr.getColorFrame() + "[DM_" + fromID + "] " + msgBuffer.substring(cur+1);
                } else if (mtype == MsgType.BROAD) {
                        msgFrame = csptr.getColorFrame() + "[" + fromID + "] " + msgBuffer;
                } else {
                        msgFrame = "";
                }
                return msgFrame;
        }


        void clear() throws IOException {
                channel.close();
                fromID = "";
                msgBuffer = "";
        }
}
