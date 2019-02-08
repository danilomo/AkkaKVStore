package com.emnify.kvcluster.backend;

import akka.actor.AbstractActor;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Scheduler;
import com.emnify.kvcluster.api.CustomLogger;
import com.emnify.kvcluster.messages.EntryMessage;
import com.emnify.kvcluster.messages.PutMessage;
import com.emnify.kvcluster.messages.TakeMessage;
import com.emnify.kvcluster.messages.TimeoutMessage;
import java.time.Duration;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
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

    @Override
    public void preStart() throws Exception {
        CustomLogger.println("Fui instanciado.");
    }
    
    

    private void putMessage(PutMessage<String, String> message) {
        CustomLogger.println("Recebi uma mensagem de put, ja posso ir embora.");
        ActorRef receiver = receivers.poll();
        receiver.tell(new EntryMessage<>(message.value()), Actor.noSender());

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
        CustomLogger.println("Chegou timeout.");
        ActorRef receiver = msg.actor();
        receiver.tell(new TimeoutMessage(), Actor.noSender());
        receivers.remove(receiver);
        
        unregisterAndStopIfEmpty();
    }

    private void unregisterAndStopIfEmpty() {
        if (receivers.isEmpty()) {
            parent.tell(new UnregisterMessage(key, self()), Actor.noSender());
            context().stop(self());
        }
    }

}
