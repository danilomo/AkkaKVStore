package com.emnify.kvcluster.frontend;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.management.javadsl.AkkaManagement;

import akka.routing.FromConfig;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import com.emnify.kvcluster.api.ConfigLocalAddress;
import com.emnify.kvcluster.backend.MessageExtractor;
import static akka.pattern.Patterns.ask;
import com.emnify.kvcluster.backend.StorageActor;
import com.emnify.kvcluster.frontend.http.HTTPApplication;
import com.emnify.kvcluster.messages.GetMessage;
import com.emnify.kvcluster.messages.HasSingleton;
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
        ActorSystem system = startActorSystem(args);

        ActorRef shardProxy = createShardProxy(system);

        ActorRef router = createBackendRouter(system);

        mainLoop(router, shardProxy);
    }

    private static void mainLoop(ActorRef router,
                                 ActorRef shardProxy) {
        Scanner scanner = new Scanner(System.in);

        while(true) {
            try {
                String str = scanner.nextLine();

                if(str.trim().equals("")){
                    router.tell(
                        new HasSingleton(),
                        ActorRef.noSender());
                    continue;
                }

                String[] arr = str.split(" ");

                CompletableFuture<Object> future1 =
                    ask(shardProxy, new GetMessage<>(arr[0], arr[1]),
                        Duration.ofMillis(1000)).toCompletableFuture();

                System.out.println(future1.get());
            }catch(Exception ex){}
        }
    }

    private static ActorRef createShardProxy(ActorSystem system) {
        return ClusterSharding
            .get(system)
            .start(
                "storage",
                Props.create(StorageActor.class),
                ClusterShardingSettings.create(system),
                new MessageExtractor()
            );
    }

    private static ActorRef createBackendRouter(final ActorSystem system) {
        ActorRef router = system.
            actorOf(
                FromConfig
                    .getInstance()
                    .props(),
                "singletonChecker"
            );

        return router;
    }

    private static ActorSystem startActorSystem(final String[] args) {
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
                "\nakka.management.http.port=" + (8080)
        ).withFallback(ConfigFactory.load("frontend"));

        ActorSystem system = ActorSystem.create("kvstore", config);
        AkkaManagement.get(system).start();

        startAkkaHttp(system, config);

        return system;
    }

    private static void startAkkaHttp(ActorSystem system, Config config) {
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final HTTPApplication app = new HTTPApplication(system);

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow =
            app
                .createRoute()
                .flow(system, materializer);

        http.bindAndHandle(routeFlow,
            ConnectHttp.toHost(
                new ConfigLocalAddress(config).get(),
                8000
            ),
            materializer
        );
    }
}
