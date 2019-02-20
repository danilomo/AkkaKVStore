package com.emnify.kvcluster.client.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import com.emnify.kvcluster.api.CustomLogger;
import com.emnify.kvcluster.messages.EntryMessage;
import com.emnify.kvcluster.messages.ReplyMessage;
import com.emnify.kvcluster.messages.TakeMessage;
import com.emnify.kvcluster.messages.TimeoutMessage;
import java.util.function.BiConsumer;

/**
 *
 * @author Danilo Oliveira
 */
public class ReceiverActor extends AbstractActor {

    private final ActorRef frontend;
    private final String key;
    private BiConsumer<ReplyMessage, ActorRef> consumer = (message, sender) -> {
        CustomLogger.println("Actor " + self() + " got message " + message + " from " + sender);
    };

    public ReceiverActor(String key, ActorRef frontend) {
        this.frontend = frontend;
        this.key = key;
    }
    
    public ReceiverActor(String key, 
            ActorRef frontend, BiConsumer<ReplyMessage, ActorRef> consumer) {
        this(key, frontend);
        this.consumer = consumer;
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
        consumer.accept(message, sender());
        listenForMessage();
    }
}
