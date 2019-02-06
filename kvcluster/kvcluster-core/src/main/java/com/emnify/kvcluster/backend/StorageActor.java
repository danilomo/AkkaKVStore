package com.emnify.kvcluster.backend;

import akka.actor.AbstractActor;
import java.util.HashMap;
import java.util.Map;
import com.emnify.kvcluster.messages.*;

/**
 *
 * @author Danilo Oliveira 
 */
public class StorageActor extends AbstractActor{    
    private final Map<String, String> map = new HashMap<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GetMessage.class, this::getMessage)
                .match(PutMessage.class, this::putMessage)
                .match(String.class, message -> message.equals("get-contents"), s -> getContents())
                .build();
    }
    
    public void getMessage(GetMessage<String> message){
        System.err.println("getMessage() -> " + message);
        String key = message.key();
        sender().tell(
                map.containsKey(key) ? new EntryMessage<>(map.get(key)) : new KeyAbsentMessage(),
                self()
        );
    }
    
    public void putMessage(PutMessage<String, String> message){
        System.err.println("putMessage() -> " + message);        
        String key = message.key();
        String value = message.value();
        map.put(key, value);
    }
    
    public void getContents(){
        System.err.println("getContents() invoked");        
        sender().tell(map.toString(), self());
    }
    
}
