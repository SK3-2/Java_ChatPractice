public class ClientManager {
    public ClientManager(EventManager* ptr_p) {
        pmptr=ptr_p;
        pmptr -> register_ClientManagaer(this);
    }

    public void register_ID(Message* Msg, int fd) {


    }

}
