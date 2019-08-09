package com.emnify.kvcluster.backend;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import akka.management.javadsl.AkkaManagement;
import com.emnify.kvcluster.messages.PutMessage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.Scanner;

/**
 * @author Danilo Oliveira
 */
public class BackendMain {

    public static void main(String[] args) {

        int port = 0;

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
        ).withFallback(ConfigFactory.load("backend"));

        ActorSystem system = ActorSystem.create("kvstore", config);

        AkkaManagement.get(system).start();

        ActorRef shardRegion =
            ClusterSharding
                .get(system)
                .start(
                  "storage",
                    Props.create(StorageActor.class),
                    ClusterShardingSettings.create(system),
                    new MessageExtractor()
                );

        Scanner scanner = new Scanner(System.in);

        while(true){
            try {
                String str = scanner.nextLine();
                String[] arr = str.split(" ");
                shardRegion.tell(new PutMessage<>(arr[0], arr[1], arr[2]), ActorRef.noSender());
            }catch(Exception ex){}
        }
    }
}
