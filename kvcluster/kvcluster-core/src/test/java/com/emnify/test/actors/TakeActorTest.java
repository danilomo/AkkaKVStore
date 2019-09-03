package com.emnify.test.actors;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import com.emnify.kvcluster.backend.StorageActor;
import com.emnify.kvcluster.messages.EntryMessage;
import com.emnify.kvcluster.messages.PutMessage;
import com.emnify.kvcluster.messages.TakeMessage;
import com.emnify.kvcluster.messages.TimeoutMessage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Danilo Oliveira
 */
public class TakeActorTest {

    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testTimeout() {
        new TestKit(system) {
            {
                ActorRef probe = getRef();
                ActorRef storage = system.actorOf(
                    Props.create(StorageActor.class, system.deadLetters()),
                    "storage-testTimeout"
                );

                storage.tell(new TakeMessage<>("","key", 1), probe);
                expectMsgClass(TimeoutMessage.class);
            }
        };
    }

    @Test
    public void testTake() {
        new TestKit(system) {
            {
                ActorRef probe = getRef();
                ActorRef storage = system.actorOf(
                    Props.create(StorageActor.class, system.deadLetters()),
                    "storage-testTake"
                );

                storage.tell(new PutMessage<>("","key-a", "valuea"), ActorRef.noSender());
                storage.tell(new TakeMessage<>("","key-a", 1), probe);
                expectMsg(new EntryMessage<>("valuea"));
            }
        };
    }

    @Test
    public void testTakeMultipleTakers1() {
        TestKit probe1 = new TestKit(system);
        TestKit probe2 = new TestKit(system);

        ActorRef storage = system.actorOf(
            Props.create(StorageActor.class, system.deadLetters()),
            "storage-testTakeMultiple"
        );

        storage.tell(new TakeMessage<>("key", 1), probe1.getRef());
        storage.tell(new TakeMessage<>("key", 1), probe2.getRef());
        storage.tell(new PutMessage<>("key", "value"), ActorRef.noSender());

        probe1.expectMsg(new EntryMessage<>("value"));
        probe2.expectMsgClass(TimeoutMessage.class);
    }

    @Test
    public void testTakeMultipleTakers2() {
        TestKit probe1 = new TestKit(system);
        TestKit probe2 = new TestKit(system);

        ActorRef storage = system.actorOf(
            Props.create(StorageActor.class, system.deadLetters()),
            "storage-testTakeMultiple2"
        );

        storage.tell(new TakeMessage<>("key", 1), probe1.getRef());
        storage.tell(new TakeMessage<>("key", 1), probe2.getRef());
        storage.tell(new PutMessage<>("key", "valuea"), ActorRef.noSender());
        storage.tell(new PutMessage<>("key", "valueb"), ActorRef.noSender());

        probe1.expectMsg(new EntryMessage<>("valuea"));
        probe2.expectMsg(new EntryMessage<>("valueb"));
    }


    @Test
    public void testUnregisteredAfterReceivingMessage() {
        TestKit probe1 = new TestKit(system);
        TestKit probe2 = new TestKit(system);

        ActorRef storage = system.actorOf(
            Props.create(StorageActor.class, system.deadLetters()),
            "testUnregistered"
        );

        storage.tell(new TakeMessage<>("key", 1), probe1.getRef());
        storage.tell(new TakeMessage<>("key", 1), probe2.getRef());
        storage.tell(new PutMessage<>("key", "valuea"), ActorRef.noSender());
        storage.tell(new PutMessage<>("key", "valueb"), ActorRef.noSender());

        probe1.expectMsg(new EntryMessage<>("valuea"));
        probe2.expectMsg(new EntryMessage<>("valueb"));

        storage.tell(new PutMessage<>("key", "valuea"), ActorRef.noSender());
        storage.tell(new PutMessage<>("key", "valueb"), ActorRef.noSender());

        probe1.expectNoMessage();
        probe2.expectNoMessage();

    }

}
