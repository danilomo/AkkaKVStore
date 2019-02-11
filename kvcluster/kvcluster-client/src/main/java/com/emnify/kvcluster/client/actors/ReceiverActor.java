package com.emnify.kvcluster.client.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import com.emnify.kvcluster.messages.EntryMessage;
import com.emnify.kvcluster.messages.TakeMessage;
import com.emnify.kvcluster.messages.TimeoutMessage;

/**
 *
 * @author danilo
 */
public class ReceiverActor extends AbstractActor {

    private final ActorRef frontend;
    private final String key;

    public ReceiverActor(String key, ActorRef frontend) {
        this.frontend = frontend;
        this.key = key;
    }

    @Override
    public void preStart() throws Exception {
        listenForMessage();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(TimeoutMessage.class, m -> this.listenForMessage())
                .match(EntryMessage.class, this::gotMessage )
                .build();
    }

    private void listenForMessage() {
        frontend.tell(new TakeMessage<>(key, 20), self());
    }

    private void gotMessage(EntryMessage<String> message) {
        System.out.println("Actor " + self() +"got message: " + message);
        listenForMessage();
    }
}
