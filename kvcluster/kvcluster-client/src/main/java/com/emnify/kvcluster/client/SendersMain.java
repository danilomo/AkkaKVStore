package com.emnify.kvcluster.client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinGroup;
import com.emnify.kvcluster.api.FrontendRefBuilder;
import com.emnify.kvcluster.client.actors.SenderActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.util.stream.IntStream;

/**
 *
 * @author Danilo Oliveira
 */
public class SendersMain {

    public static void main(String[] args) {

        args = new String[]{"10", "0.5", "3000"};

        int numberOfSenders = Integer.parseInt(args[0]);
        double rate = Double.parseDouble(args[1]);
        int port = Integer.parseInt(args[2]);

        Config config = ConfigFactory.parseString(
                "akka.remote.netty.tcp.port=" + port)
                .withFallback(ConfigFactory.load("client"));

        ActorSystem system = ActorSystem.create("kvstore", config);

        ActorRef frontend = new FrontendRefBuilder(system)
                .withConfig(config)
                .withName("frontend")
                .withStringList("akka.cluster.seed-nodes", s -> s + "/user/frontend")
                .withGroup(paths -> new RoundRobinGroup(paths))
                .build();        

        startSenders(system, frontend, numberOfSenders, rate);
    }

    private static void startSenders(ActorSystem system, ActorRef frontend,
            int numberOfSenders, double rate) {

        IntStream.range(0, numberOfSenders).forEach(
                i -> system.actorOf(Props.create(SenderActor.class, frontend, rate))
        );
    }
}
