package com.emnify.kvcluster.frontend;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import com.emnify.kvcluster.api.ConsistentHash;
import com.emnify.kvcluster.messages.GetMessage;
import com.emnify.kvcluster.messages.JoinMessage;
import com.emnify.kvcluster.messages.PutMessage;
import com.emnify.kvcluster.messages.TakeMessage;
import java.util.ArrayList;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Danilo Oliveira
 */
public class FrontendActor extends AbstractActor {

    private final ConsistentHash<UUID> consistentHash;
    private final Map<UUID, ActorRef> nodes;

    public FrontendActor() {
        consistentHash = new ConsistentHash<>(obj -> obj.toString().hashCode(), 1024, new ArrayList<>());
        nodes = new HashMap<>();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JoinMessage.class, this::joinMessage)
                .match(PutMessage.class, this::putMessage)
                .match(GetMessage.class, this::getMessage)
                .match(TakeMessage.class, this::takeMessage)
                .build();
    }

    private void joinMessage(JoinMessage message) {
        consistentHash.add(message.uuid());
        nodes.put(message.uuid(), message.actor());
    }

    private void putMessage(PutMessage<String, String> message) {
        lookupActor(message.key()).tell(message, sender());
    }

    private void getMessage(GetMessage<String> message) {
        lookupActor(message.key()).tell(message, sender());
    }
    
    private void takeMessage(TakeMessage<String> message) {
        lookupActor(message.key()).tell(message, sender());
    }    

    private ActorRef lookupActor(String key) {
        UUID uuid = consistentHash.get(key);
        ActorRef actor = nodes.get(uuid);
        return actor;
    }

}
