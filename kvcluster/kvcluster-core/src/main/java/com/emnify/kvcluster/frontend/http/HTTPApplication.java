package com.emnify.kvcluster.frontend.http;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import com.emnify.kvcluster.messages.AddressReply;
import com.emnify.kvcluster.messages.HasSingleton;
import com.emnify.kvcluster.messages.Message;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import static java.util.stream.Collectors.toList;
import static akka.pattern.Patterns.ask;

/**
 * @author Danilo Oliveira
 */
public class HTTPApplication extends AllDirectives {

    private final ActorSystem system;

    public HTTPApplication(ActorSystem system) {
        this.system = system;
    }

    static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> com) {
        return CompletableFuture.allOf(com.toArray(new CompletableFuture<?>[com.size()]))
            .thenApply(v -> com.stream()
                .map(CompletableFuture::join)
                .collect(toList())
            );
    }

    public Route createRoute() {
        Route singletonLocation = get(
            () -> path(
                "singleton",
                () -> onSuccess(
                    getSingletonLocation(),
                    t -> complete(
                        StatusCodes.OK,
                        t,
                        Jackson.<Message>marshaller()
                    )
                )
            )
        );

        return singletonLocation;
    }

    private CompletableFuture<Message> getSingletonLocation() {
        ActorSelection selection = system.actorSelection("/user/singletonChecker");

        CompletableFuture<Object> future = ask(
            selection,
            new HasSingleton(),
            Duration.ofMillis(1000)
        ).toCompletableFuture().exceptionally((t) -> null );

        return future.thenCompose(obj ->
            CompletableFuture.supplyAsync(() ->
                new AddressReply(obj.toString())
            )
        );
    }
}
