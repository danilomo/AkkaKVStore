package com.emnify.kvcluster.backend;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Address;
import com.emnify.kvcluster.messages.HasSingleton;
import java.time.Duration;

public class SingletonCheckerActor extends AbstractActor {

    private final String address;

    public SingletonCheckerActor(final String address) {
        this.address = address;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(String.class, this::hello)
            .match(HasSingleton.class, msg -> hasSingleton())
            .build();
    }

    private void hello(String msg) {
        System.out.println(">>>> " + msg);
    }

    private void hasSingleton() {
        ActorSelection singleton = getContext().
            actorSelection("/system/sharding/storageCoordinator/singleton" +
                "/coordinator");
        final ActorRef sender = sender();

        singleton
            .resolveOne(Duration.ofMillis(200))
            .toCompletableFuture()
            .thenAccept(
                ref -> sender.tell(address, self())
            );
    }
}
