package com.emnify.kvcluster.backend;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import java.util.HashMap;
import java.util.Map;
import com.emnify.kvcluster.messages.*;
import java.util.UUID;

/**
 *
 * @author Danilo Oliveira
 */
public class StorageActor extends AbstractActor {

    private final Map<String, String> map = new HashMap<>();
    private final ActorRef frontend;
    private final Map<String, ActorRef> takeActors = new HashMap<>();
    private UUID uuid;

    public StorageActor(ActorRef frontend) {
        this.frontend = frontend;
    }

    @Override
    public void preStart() {
        uuid = UUID.randomUUID();
        frontend.tell(new JoinMessage(uuid, self()), self());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GetMessage.class, this::getMessage)
                .match(PutMessage.class, this::putMessage)
                .match(TakeMessage.class, this::takeMessage)
                .match(UnregisterMessage.class, this::unregisterMessage)
                .match(String.class, message -> message.equals("get-contents"), s -> getContents())
                .build();
    }

    private void getMessage(GetMessage<String> message) {
        String key = message.key();
        sender().tell(
                map.containsKey(key) ? new EntryMessage<>(map.get(key)) : new KeyAbsentMessage(),
                self()
        );
    }

    private void putMessage(PutMessage<String, String> message) {
        String key = message.key();
        String value = message.value();

        if (takeActors.containsKey(key)) {
            takeActors.get(key).tell(message, self());
        } else {
            map.put(key, value);
        }
    }

    private void takeMessage(TakeMessage<String> message) {
        String key = message.key();

        if (map.containsKey(key)) {
            Message response = new EntryMessage<>(map.get(key));
            map.remove(key);
            sender().tell(response, self());
        } else {
            ActorRef takeActor = takeActors.get(key);
            if (takeActor == null) {
                takeActor = context().actorOf(
                        Props.create(TakeActor.class, self(), key)
                );
                takeActors.put(key, takeActor);
            }
            takeActor.tell(message, sender());
        }
    }

    private void unregisterMessage(UnregisterMessage msg) {
        takeActors.remove(msg.key());
    }

    public void getContents() {
        sender().tell(map.toString(), self());
    }

}
