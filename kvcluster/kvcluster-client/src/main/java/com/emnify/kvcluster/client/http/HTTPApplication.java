package com.emnify.kvcluster.client.http;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.PathMatchers;
import akka.http.javadsl.server.Route;
import static akka.pattern.Patterns.ask;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import static com.emnify.kvcluster.client.actors.ReceiverActor.GET_MESSAGES;

/**
 *
 * @author Danilo Oliveira
 */
public class HTTPApplication extends AllDirectives {

    private final ActorSystem system;

    public HTTPApplication(ActorSystem system) {
        this.system = system;
    }

    public Route createRoute() {
        return get(
                () -> pathPrefix(
                        "keys",
                        () -> path(
                                PathMatchers.segment(),
                                (String key) -> {
                                    return onSuccess(getReceivedMessages(key), (t) -> {
                                        return complete(StatusCodes.OK, t, Jackson.<List>marshaller());
                                    });
                                }
                        )
                )
        );
    }

    private CompletableFuture<List<String>> getReceivedMessages(String key) {
        ActorSelection selection = system.actorSelection("/user/" + key);
        CompletableFuture<Object> future = ask(
                selection,
                GET_MESSAGES,
                Duration.ofMillis(1000)
        ).toCompletableFuture();

        return future.thenCompose(obj -> {
            if (obj instanceof List) {
                return CompletableFuture.supplyAsync(() -> (List<String>) obj);
            }else {
                return CompletableFuture.supplyAsync(() -> (List<String>) new ArrayList<String>());
            }            
        });
    }
}
