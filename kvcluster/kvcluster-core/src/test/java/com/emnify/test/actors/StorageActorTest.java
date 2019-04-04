package com.emnify.test.actors;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import com.emnify.kvcluster.backend.StorageActor;
import com.emnify.kvcluster.messages.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Danilo Oliveira
 */
public class StorageActorTest {

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
    public void testPutAndGet() {
        new TestKit(system) {
            {
                ActorRef storage = createStorageActor("testPutAndGet");
                storage.tell(new PutMessage<>("key", "value"), Actor.noSender());
                storage.tell(new GetMessage<>("key"), getRef());
                expectMsgEquals(new EntryMessage<>("value"));
            }
        };
    }

    @Test
    public void testGetNotExists() {
        new TestKit(system) {
            {
                ActorRef storage = createStorageActor("testGetNotExists");
                storage.tell(new PutMessage<>("key-a", "value"), Actor.noSender());
                storage.tell(new GetMessage<>("key-b"), getRef());
                expectMsgClass(KeyAbsentMessage.class);
            }
        };
    }

    @Test
    public void testJoin() {
        new TestKit(system) {
            {
                createStorageActor("testJoin", getRef());
                expectMsgClass(JoinMessage.class);
            }
        };
    }

    private ActorRef createStorageActor(String name) {
        return createStorageActor(name, system.deadLetters());
    }

    private ActorRef createStorageActor(String name, ActorRef frontend) {
        return system.actorOf(
            Props.create(StorageActor.class, frontend),
            name
        );
    }

}
