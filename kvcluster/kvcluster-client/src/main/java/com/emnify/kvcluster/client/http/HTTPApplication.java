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
import com.emnify.kvcluster.client.random.RandomStrings;
import java.util.Arrays;
import static java.util.stream.Collectors.toList;

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
        Route getKey = get(
            () -> pathPrefix(
                "key",
                () -> path(
                    PathMatchers.segment(),
                    (String key) -> {
                        return onSuccess(getMessagesByKey(key), (t) -> {
                            return complete(StatusCodes.OK, t, Jackson.<List>marshaller());
                        });
                    }
                )
            )
        );

        Route allKeys = get(
            () -> path(
                "keys",
                () -> {
                    return onSuccess(
                        getAllMessages(), 
                        (t) -> complete(StatusCodes.OK, t, Jackson.<List>marshaller())
                    );
                }
            )
        );

        return concat(getKey, allKeys);
    }

    private CompletableFuture<List<String>> getMessagesByKey(String key) {
        ActorSelection selection = system.actorSelection("/user/" + key);
        
        CompletableFuture<Object> future = ask(
                selection,
                GET_MESSAGES,
                Duration.ofMillis(1000)
        ).toCompletableFuture().exceptionally((t) -> new ArrayList<>() );

        return future.thenCompose(obj -> {
            if (obj instanceof List) {
                return CompletableFuture.supplyAsync(() -> (List<String>) obj);
            } else {
                return CompletableFuture.supplyAsync(() -> (List<String>) new ArrayList<String>());
            }
        });
    }

    private CompletableFuture<List<List<String>>> getAllMessages() {
        List<String> keys = Arrays.asList(RandomStrings.KEYS);
        
        List<CompletableFuture<List<String>>> futures = keys
                .stream()
                .map( key -> getMessagesByKey(key) )
                .collect(toList());
        
        return sequence(futures);
    }

    static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> com) {
        return CompletableFuture.allOf(com.toArray(new CompletableFuture<?>[com.size()]))
                .thenApply(v -> com.stream()
                .map(CompletableFuture::join)
                .collect(toList())
                );
    }
}
