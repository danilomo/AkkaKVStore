package com.emnify.kvcluster.playground;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.ConsistentHashingGroup;
import com.emnify.kvcluster.backend.StorageActor;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author danilo
 */
public class Playground {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("test");

        class A extends AbstractActor {

            @Override
            public void preStart() throws Exception {
                ActorRef s1 = context().actorOf(Props.create(StorageActor.class), "s1");
                ActorRef s2 = context().actorOf(Props.create(StorageActor.class), "s2");
                ActorRef s3 = context().actorOf(Props.create(StorageActor.class), "s3");
                ActorRef s4 = context().actorOf(Props.create(StorageActor.class), "s4");
                ActorRef s5 = context().actorOf(Props.create(StorageActor.class), "s5");

                List<ActorRef> l1 = Arrays.asList(s1, s2, s3, s4, s5);
                List<ActorRef> l2 = Arrays.asList(s1, s2, s3, s4);

                List<String> paths1 = l1.stream().map(ref -> ref.path().toString()).collect(Collectors.toList());
                //List<String> paths2 = l2.stream().map(ref -> ref.path().toString()).collect(Collectors.toList());

                ConsistentHashingGroup group = new ConsistentHashingGroup(paths1);

                ActorRef router = context().actorOf(group.props());

                paths1.forEach(f -> {
                    System.out.println(group.routeeFor(f, context()));
                });
            }

            @Override
            public Receive createReceive() {
                return receiveBuilder().build();
            }

        }

    }
}
