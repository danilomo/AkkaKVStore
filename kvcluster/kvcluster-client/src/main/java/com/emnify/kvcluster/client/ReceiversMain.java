package com.emnify.kvcluster.client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinGroup;
import com.emnify.kvcluster.api.FrontendRefBuilder;
import com.emnify.kvcluster.client.actors.ReceiverActor;
import com.emnify.kvcluster.client.random.RandomStrings;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 *
 * @author Danilo Oliveira
 */
public class ReceiversMain {

    public static void main(String[] args) {
        int receivers = Integer.parseInt(args[0]);
        int port = Integer.parseInt(args[1]);

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

        //TODO - Receivers shouldn't be larger than the size of the used array        
        for (int i = 0; i < receivers; i++) {
            String key = RandomStrings.KEYS[i];
            system.actorOf(Props.create(ReceiverActor.class, key, frontend));
        }
    }
}
