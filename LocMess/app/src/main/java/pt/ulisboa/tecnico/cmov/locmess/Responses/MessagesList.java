package pt.ulisboa.tecnico.cmov.locmess.Responses;

import pt.ulisboa.tecnico.cmov.locmess.Domain.DeliverableMessage;

import java.util.List;

/**
 * Created by joao on 3/25/17.
 */
public class MessagesList extends Response{
    private List<DeliverableMessage> _messages;

    public MessagesList(boolean successful, String message, List<DeliverableMessage> messages) {
        _messages = messages;
    }

    public List<DeliverableMessage> getMessages(){
        return _messages;
    }

}
