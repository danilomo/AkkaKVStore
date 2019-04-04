package com.emnify.kvcluster.client.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Scheduler;
import com.emnify.kvcluster.client.random.*;
import com.emnify.kvcluster.messages.PutMessage;
import com.emnify.kvcluster.messages.RequestMessage;

import java.time.Duration;

/**
 * @author Danilo Oliveira
 */
public class SenderActor extends AbstractActor {

    public static final StopMessage STOP = new StopMessage();
    private static final SendMessage SEND = new SendMessage();
    private final RandomGenerator generator;
    private final ActorRef frontend;
    private final StringGenerator keyGenerator;
    private final StringGenerator valueGenerator;
    private final long maxMessages;
    private Scheduler scheduler;
    private boolean stopped;
    private long messagesSent = 0;
    private Cancellable event = Cancellable.alreadyCancelled();

    public SenderActor(ActorRef frontend, double rate) {
        generator = new ExponentialGenerator(rate);
        this.frontend = frontend;
        this.keyGenerator = new RandomStringFromList(RandomStrings.KEYS);
        this.valueGenerator = new RandomStringFromList(RandomStrings.VALUES);
        this.maxMessages = Long.MAX_VALUE;
    }

    public SenderActor(ActorRef frontend, RandomGenerator generator,
                       StringGenerator keyGenerator, StringGenerator valueGenerator,
                       long maxMessages
    ) {
        this.frontend = frontend;
        this.generator = generator;
        this.keyGenerator = keyGenerator;
        this.valueGenerator = valueGenerator;
        this.maxMessages = maxMessages;
    }

    @Override
    public void preStart() throws Exception {
        scheduleNextMessage();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(SendMessage.class, m -> sendRandomMessage())
            .match(StopMessage.class, m -> {
                event.cancel();
                context().stop(self());
            })
            .build();
    }

    private void sendRandomMessage() {
        if (messagesSent >= maxMessages) {
            return;
        }

        scheduleNextMessage();
        RequestMessage request = new PutMessage(keyGenerator.generate(), valueGenerator.generate());
        System.out.println(request);
        frontend.tell(request, self());
        messagesSent++;
    }

    private void scheduleNextMessage() {
        scheduler = context().system().scheduler();
        long next = (long) (generator.generate() * 1000);

        event = scheduler.scheduleOnce(
            Duration.ofMillis(next),
            self(),
            SEND,
            context().dispatcher(),
            null
        );
    }

    private static class SendMessage {
    }

    public static class StopMessage {
    }
}
