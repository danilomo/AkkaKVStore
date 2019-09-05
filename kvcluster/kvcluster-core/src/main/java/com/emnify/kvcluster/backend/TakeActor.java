package com.emnify.kvcluster.backend;

import akka.actor.AbstractActor;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Scheduler;
import com.emnify.kvcluster.messages.EntryMessage;
import com.emnify.kvcluster.messages.PutMessage;
import com.emnify.kvcluster.messages.TakeMessage;
import com.emnify.kvcluster.messages.TimeoutMessage;

import java.time.Duration;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Danilo Oliveira
 */
public class TakeActor extends AbstractActor {

    private final ActorRef parent;
    private final Queue<ActorRef> receivers;
    private final String key;

    public TakeActor(ActorRef parent, String key) {
        this.parent = parent;
        this.receivers = new LinkedList<>();
        this.key = key;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(PutMessage.class, this::putMessage)
            .match(TakeMessage.class, this::takeMessage)
            .match(TaketimeoutMessage.class, this::timeoutMessage)
            .build();
    }

    private void putMessage(PutMessage<String, String> message) {
        ActorRef receiver = receivers.poll();
        receiver.tell(new EntryMessage<>(message.value()), self());

        unregisterAndStopIfEmpty();
    }

    private void takeMessage(TakeMessage<String> message) {
        receivers.add(sender());

        Scheduler scheduler = context().system().scheduler();
        scheduler.scheduleOnce(
            Duration.ofSeconds(message.timeout()),
            self(),
            new TaketimeoutMessage(sender()),
            context().system().dispatcher(),
            null
        );
    }

    private void timeoutMessage(TaketimeoutMessage msg) {
        ActorRef receiver = msg.actor();
        receiver.tell(new TimeoutMessage(), ActorRef.noSender());
        receivers.remove(receiver);

        unregisterAndStopIfEmpty();
    }

    private void unregisterAndStopIfEmpty() {
        if (receivers.isEmpty()) {
            parent.tell(new UnregisterMessage(key, self()), ActorRef.noSender());
            context().stop(self());
        }
    }

}
