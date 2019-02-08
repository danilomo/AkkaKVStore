package com.emnify.kvcluster.backend;

import akka.actor.AbstractActor;
import akka.actor.Actor;
import akka.actor.ActorRef;
import com.emnify.kvcluster.messages.EntryMessage;
import com.emnify.kvcluster.messages.PutMessage;
import com.emnify.kvcluster.messages.TimeoutMessage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Danilo Oliveira
 */
public class TakeActor extends AbstractActor {

    private final ActorRef parent;
    private final List<ActorRef> receivers;
    private final String key;

    public TakeActor(ActorRef parent, String key) {
        this.parent = parent;
        this.receivers = new ArrayList<>();
        this.key = key;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PutMessage.class, this::putMessage)
                .match(TimeoutMessage.class, this::timeoutMessage)
                .build();
    }

    private void putMessage(PutMessage<String, String> message) {        
        ActorRef receiver = receivers.get(0);
        receivers.remove(0);
        
        receiver.tell( new EntryMessage<>(message.value()), Actor.noSender() );
        
        if(receivers.isEmpty()){
            unregisterItself();
            context().stop(self());
        }
    }

    private void timeoutMessage(TimeoutMessage msg) {       

    }

    private void unregisterItself() {
        parent.tell(new UnregisterMessage(key, self()), Actor.noSender());       
    }

}
