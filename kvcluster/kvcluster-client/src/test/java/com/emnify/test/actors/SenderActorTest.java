package com.emnify.test.actors;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import com.emnify.kvcluster.backend.StorageActor;
import com.emnify.kvcluster.client.actors.SenderActor;
import com.emnify.kvcluster.client.random.RandomGenerator;
import com.emnify.kvcluster.client.random.StringGenerator;
import com.emnify.kvcluster.frontend.FrontendActor;
import java.time.Duration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Danilo Oliveira
 */
public class SenderActorTest {

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
    public void testSend10Messages() {
        new TestKit(system) {
            {
                system.actorOf(
                        Props.create(
                                SenderActor.class,
                                getRef(),
                                (RandomGenerator) () -> 0.3,
                                (StringGenerator) () -> "key",
                                (StringGenerator) () -> "value",
                                10l
                        )
                );

                receiveN(10, Duration.ofSeconds(5));
            }
        };
    }

    @Test
    public void testStop() {
        new TestKit(system) {
            {
                ActorRef sender = system.actorOf(
                        Props.create(
                                SenderActor.class,
                                getRef(),
                                (RandomGenerator) () -> 0.3,
                                (StringGenerator) () -> "key",
                                (StringGenerator) () -> "value",
                                1000l
                        )
                );

                system.scheduler().scheduleOnce(
                        Duration.ofMillis(1300),
                        sender,
                        SenderActor.STOP,
                        system.dispatcher(),
                        Actor.noSender()                       
                );

                receiveN(4, Duration.ofMillis(3000));
            }
        };
    }
}
