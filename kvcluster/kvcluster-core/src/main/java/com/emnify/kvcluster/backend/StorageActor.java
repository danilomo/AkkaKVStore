package com.emnify.kvcluster.backend;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.routing.BroadcastGroup;
import java.util.HashMap;
import java.util.Map;
import com.emnify.kvcluster.messages.*;
import java.util.List;

/**
 *
 * @author Danilo Oliveira
 */
public class StorageActor extends AbstractActor {

    private final Map<String, String> map = new HashMap<>();
    private final List<String> paths;

    public StorageActor(List<String> paths) {
        this.paths = paths;
    }

    @Override
    public void preStart() throws Exception {
        ActorRef router
                = getContext().actorOf(new BroadcastGroup(paths).props(), "router");
        router.tell(new JoinMessage(self()), self());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GetMessage.class, this::getMessage)
                .match(PutMessage.class, this::putMessage)
                .match(String.class, message -> message.equals("get-contents"), s -> getContents())
                .build();
    }

    public void getMessage(GetMessage<String> message) {
        String key = message.key();
        sender().tell(
                map.containsKey(key) ? new EntryMessage<>(map.get(key)) : new KeyAbsentMessage(),
                self()
        );
    }

    public void putMessage(PutMessage<String, String> message) {
        String key = message.key();
        String value = message.value();
        map.put(key, value);
    }

    public void getContents() {
        System.err.println("getContents() invoked");
        sender().tell(map.toString(), self());
    }

}
