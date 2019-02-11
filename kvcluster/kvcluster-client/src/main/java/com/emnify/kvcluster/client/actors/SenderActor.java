package com.emnify.kvcluster.client.actors;

import com.emnify.kvcluster.client.random.ExponentialGenerator;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Scheduler;
import com.emnify.kvcluster.client.random.RandomGenerator;
import com.emnify.kvcluster.client.random.RandomStringFromList;
import com.emnify.kvcluster.client.random.RandomStrings;
import com.emnify.kvcluster.client.random.StringGenerator;
import com.emnify.kvcluster.messages.PutMessage;
import com.emnify.kvcluster.messages.RequestMessage;
import java.time.Duration;

/**
 *
 * @author Danilo Oliveira
 */
public class SenderActor extends AbstractActor {

    private Scheduler scheduler;
    private final RandomGenerator generator;
    private final ActorRef frontend;
    private boolean stopped;
    private final StringGenerator keyGenerator;
    private final StringGenerator valueGenerator;

    public SenderActor(ActorRef frontend, double rate) {
        generator = new ExponentialGenerator(rate);
        this.frontend = frontend;
        this.keyGenerator = new RandomStringFromList(RandomStrings.KEYS);
        this.valueGenerator = new RandomStringFromList(RandomStrings.VALUES);
    }

    @Override
    public void preStart() throws Exception {
        scheduleNextMessage();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SendMessage.class, (m) -> sendRandomMessage())
                .build();
    }

    private void sendRandomMessage() {
        if (stopped) {
            return;
        }

        scheduleNextMessage();
        RequestMessage request = new PutMessage(keyGenerator.generate(), valueGenerator.generate());
        System.out.println(request);
        frontend.tell(request, self());
    }

    private void scheduleNextMessage() {
        scheduler = context().system().scheduler();
        long next = (long) (generator.generate() * 1000);

        scheduler.scheduleOnce(
                Duration.ofMillis(next),
                self(),
                SEND_MESSAGE,
                context().dispatcher(),
                null
        );
    }

    private static class SendMessage {
    }
    private static final SendMessage SEND_MESSAGE = new SendMessage();

    public static class CounterGenerator implements StringGenerator {

        private final String base;
        private int counter;

        public CounterGenerator(String base) {
            this.base = base;
            this.counter = 1;
        }

        @Override
        public String generate() {
            String result = base + "_" + counter;
            counter++;
            return result;
        }

    }
}
