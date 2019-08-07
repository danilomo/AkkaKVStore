package com.emnify.kvcluster.frontend;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import akka.management.javadsl.AkkaManagement;


import com.emnify.kvcluster.backend.MessageExtractor;

import static akka.pattern.Patterns.ask;

import com.emnify.kvcluster.backend.StorageActor;
import com.emnify.kvcluster.messages.GetMessage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.time.Duration;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

/**
 * @author Danilo Oliveira
 */
public class FrontendMain {

    public static void main(String[] args) {

        int port = 2551;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ne) {
                System.err.println("Invalid port.");
                System.exit(1);
            }
        }

        Config config = ConfigFactory.parseString(
            "akka.remote.netty.tcp.port=" + port +
                "\nakka.management.http.port=" + (port + 2000)
        ).withFallback(ConfigFactory.load("frontend"));

        ActorSystem system = ActorSystem.create("kvstore", config);

        if(port == 2551){
            AkkaManagement.get(system).start();
        }

        ActorRef shardProxy =
            ClusterSharding
                .get(system)
                .start(
                    "storage",
                    Props.create(StorageActor.class),
                    ClusterShardingSettings.create(system),
                    new MessageExtractor()
                );

        Scanner scanner = new Scanner(System.in);

        while(true) {
            String str = scanner.nextLine();
            String[] arr = str.split(" ");

            CompletableFuture<Object> future1 =
                ask(shardProxy, new GetMessage<>(arr[0], arr[1]),
                    Duration.ofMillis(1000)).toCompletableFuture();
            try {
                System.out.println(future1.get());
            }catch(Exception ex){}
        }
    }
}
