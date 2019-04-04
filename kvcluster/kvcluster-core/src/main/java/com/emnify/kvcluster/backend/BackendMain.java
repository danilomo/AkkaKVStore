package com.emnify.kvcluster.backend;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.BroadcastGroup;
import com.emnify.kvcluster.actors.StopNodeActor;
import com.emnify.kvcluster.api.FrontendRefBuilder;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * @author Danilo Oliveira
 */
public class BackendMain {

    public static void main(String[] args) {

        int port = 2551;

        args = new String[]{"2554"};

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ne) {
                System.err.println("Invalid port.");
                System.exit(1);
            }
        }

        Config config = ConfigFactory.parseString(
            "akka.remote.netty.tcp.port=" + port)
            .withFallback(ConfigFactory.load("backend"));

        ActorSystem system = ActorSystem.create("kvstore", config);

        ActorRef frontend = new FrontendRefBuilder(system)
            .withConfig(config)
            .withName("frontend")
            .withStringList("akka.cluster.seed-nodes", s -> s + "/user/frontend")
            .withGroup(paths -> new BroadcastGroup(paths))
            .build();

        system.actorOf(Props.create(StorageActor.class, frontend), "storage");
        system.actorOf(Props.create(StopNodeActor.class), "stop");
    }
}
