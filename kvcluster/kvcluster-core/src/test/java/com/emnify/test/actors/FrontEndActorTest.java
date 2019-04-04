package com.emnify.test.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import com.emnify.kvcluster.backend.StorageActor;
import com.emnify.kvcluster.frontend.FrontendActor;
import com.emnify.kvcluster.messages.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Danilo Oliveira
 */
public class FrontEndActorTest {

    static ActorSystem system;
    static ActorRef frontend;
    static ActorRef storage;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
        frontend = system.actorOf(Props.create(FrontendActor.class), "frontend");
        storage = system.actorOf(
            Props.create(StorageActor.class, frontend),
            "storage"
        );
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testPutAndGet() {
        new TestKit(system) {
            {
                frontend.tell(new PutMessage<>("key", "value"), getRef());
                frontend.tell(new GetMessage<>("key"), getRef());
                expectMsgEquals(new EntryMessage<>("value"));
            }
        };
    }

    @Test
    public void testTakeKeyExists() {
        new TestKit(system) {
            {
                frontend.tell(new PutMessage<>("key-a", "value"), getRef());
                frontend.tell(new TakeMessage<>("key-a", 1), getRef());
                expectMsgEquals(new EntryMessage<>("value"));
            }
        };
    }

    @Test
    public void testTakeKeyNotExists() {
        new TestKit(system) {
            {
                frontend.tell(new TakeMessage<>("key-c", 1), getRef());
                expectMsgClass(TimeoutMessage.class);
            }
        };
    }
}
