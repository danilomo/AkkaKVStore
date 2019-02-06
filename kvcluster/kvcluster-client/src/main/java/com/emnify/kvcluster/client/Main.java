package com.emnify.kvcluster.client;

import akka.actor.Actor;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.emnify.kvcluster.messages.PutMessage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import scala.concurrent.duration.Duration;
import static akka.pattern.PatternsCS.ask;
import static akka.pattern.PatternsCS.pipe;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author Danilo Oliveira
 */
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        int port = 3000;

        Config config = ConfigFactory.parseString(
                "akka.remote.netty.tcp.port=" + port)
                .withFallback(ConfigFactory.load("client"));

        // Create an Akka system
        ActorSystem system = ActorSystem.create("kvstore", config);

        ActorSelection ref = system.
                actorSelection("akka.tcp://kvstore@127.0.0.1:2554/user/storageActor");
        
        Thread.sleep(10000);
        System.out.println("Enviando mensagens.");

        ref.tell(new PutMessage<>("um",   "1"), Actor.noSender());
        ref.tell(new PutMessage<>("dois", "2"), Actor.noSender());
        ref.tell(new PutMessage<>("tres", "3"), Actor.noSender());

        Timeout t = new Timeout(Duration.create(5, TimeUnit.SECONDS));

        CompletableFuture<Object> future1
                = ask(ref, "get-contents", 1000).toCompletableFuture();
        
        System.err.println(future1.get());

    }
}
