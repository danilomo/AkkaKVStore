package com.emnify.kvcluster.client.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import com.emnify.kvcluster.api.CustomLogger;
import com.emnify.kvcluster.messages.EntryMessage;
import com.emnify.kvcluster.messages.ReplyMessage;
import com.emnify.kvcluster.messages.TakeMessage;
import com.emnify.kvcluster.messages.TimeoutMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 *
 * @author Danilo Oliveira
 */
public class ReceiverActor extends AbstractActor {

    private final ActorRef frontend;
    private final String key;
    private List<String> messages;

    private BiConsumer<ReplyMessage, ActorRef> consumer = (message, sender) -> {
        CustomLogger.println("Actor " + self() + " got message " + message + " from " + sender);
        messages.add(message.toString());
    };

    public ReceiverActor(String key, ActorRef frontend) {
        this.frontend = frontend;
        this.key = key;
        messages = new ArrayList<>();
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
                .match(EntryMessage.class, this::gotMessage)
                .match(GetMessages.class, m -> this.getMessages())
                .build();
    }

    private void listenForMessage() {
        frontend.tell(new TakeMessage<>(key, 20), self());
    }

    private void gotMessage(EntryMessage<String> message) {
        consumer.accept(message, sender());
        listenForMessage();
    }

    private void getMessages() {
        sender().tell(messages, self());
    }

    public static class GetMessages {
    }
    public static GetMessages GET_MESSAGES = new GetMessages();
}
