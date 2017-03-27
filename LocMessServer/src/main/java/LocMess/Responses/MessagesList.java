package LocMess.Responses;

import LocMess.Domain.Locations.Location;
import LocMess.Domain.Message;

import java.util.Enumeration;
import java.util.List;

/**
 * Created by joao on 3/25/17.
 */
public class MessagesList extends Response{
    private List<Message> _messages;

    public MessagesList(List<Message> messages) {
        super(true);
        _messages = messages;
    }

    public List<Message> getMessages(){
        return _messages;
    }

}
