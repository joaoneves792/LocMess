package LocMess.Responses;

import java.util.Enumeration;
import java.util.Map;

/**
 * Created by joao on 3/25/17.
 */
public class PossibleKeysList extends Response{
    private Enumeration<String> _keys;

    public PossibleKeysList(Enumeration<String> keys) {
        super(true);
        _keys = keys;
    }

    public Enumeration<String> getKeys(){
        return _keys;
    }

}
