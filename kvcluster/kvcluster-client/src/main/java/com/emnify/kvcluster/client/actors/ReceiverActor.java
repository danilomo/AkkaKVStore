package com.emnify.kvcluster.client.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Scheduler;
import com.emnify.kvcluster.messages.EntryMessage;
import com.emnify.kvcluster.messages.ReplyMessage;
import com.emnify.kvcluster.messages.TakeMessage;
import com.emnify.kvcluster.messages.TimeoutMessage;
import java.time.Duration;
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
    private Cancellable timeoutEvent = Cancellable.alreadyCancelled();
    private static final int TIMEOUT_IN_SECONDS = 20;

    private BiConsumer<ReplyMessage, ActorRef> consumer = (message, sender) -> {
        messages.add("<" + message + ", " + sender + ">");
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
                .match(EntryMessage.class, this::gotMessage)
                .match(GetMessages.class, m -> this.getMessages())
                .match(TimeoutMessage.class, m -> this.listenForMessage())
                .match(InternalTimeout.class, m -> this.listenForMessage())                
                .build();
    }

    private void listenForMessage() {

        timeoutEvent.cancel();
        frontend.tell(new TakeMessage<>(key, TIMEOUT_IN_SECONDS), self());

        Scheduler scheduler = context().system().scheduler();
        scheduler.scheduleOnce(
                Duration.ofSeconds(TIMEOUT_IN_SECONDS + 5),
                self(),
                INTERNAL_TIMEOUT,
                context().system().dispatcher(),
                null
        );
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
    
    public static class InternalTimeout {
    }
    
    public static InternalTimeout INTERNAL_TIMEOUT = new InternalTimeout();
}
