package com.emnify.kvcluster.client;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.routing.RoundRobinGroup;
import akka.stream.ActorMaterializer;
import com.emnify.kvcluster.api.FrontendRefBuilder;
import com.emnify.kvcluster.client.actors.ReceiverActor;
import com.emnify.kvcluster.client.http.HTTPApplication;
import com.emnify.kvcluster.client.random.RandomStrings;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import akka.stream.javadsl.Flow;

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
            system.actorOf(Props.create(ReceiverActor.class, key, frontend), key);
        }
        try {
            startHTTPServer(system);
        } catch (IOException ex) {
            Logger.getLogger(ReceiversMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void startHTTPServer(ActorSystem system) throws IOException {
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final HTTPApplication app = new HTTPApplication(system);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system, materializer);
        http.bindAndHandle(routeFlow,
                ConnectHttp.toHost("192.168.1.7", 8080), materializer);
    }
}
