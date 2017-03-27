package LocMess.Responses;

import LocMess.Domain.DeliverableMessage;
import LocMess.Domain.Message;

import java.util.List;

/**
 * Created by joao on 3/25/17.
 */
public class DeliverMessagesList extends Response{
    private List<DeliverableMessage> _messages;

    public DeliverMessagesList(List<DeliverableMessage> messages) {
        super(true);
        _messages = messages;
    }

    public List<DeliverableMessage> getMessages(){
        return _messages;
    }

}
