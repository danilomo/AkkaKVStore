package com.emnify.kvcluster.frontend.http;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.cluster.ddata.Replicator.Get;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpEntity;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.PathMatcher2;
import akka.http.javadsl.server.PathMatcher3;
import akka.http.javadsl.server.PathMatchers;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.unmarshalling.Unmarshaller;

import com.emnify.kvcluster.messages.AddressReply;
import com.emnify.kvcluster.messages.EntryMessage;
import com.emnify.kvcluster.messages.GetMessage;
import com.emnify.kvcluster.messages.HasSingleton;
import com.emnify.kvcluster.messages.Message;
import com.emnify.kvcluster.messages.PutMessage;
import com.emnify.kvcluster.messages.PutRequest;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static akka.pattern.Patterns.ask;
import static akka.http.javadsl.server.PathMatchers.*;

/**
 * @author Danilo Oliveira
 */
public class HTTPApplication extends AllDirectives {

  private final ActorSystem system;

  public HTTPApplication(ActorSystem system) {
    this.system = system;
  }

  public Route createRoute() {
    return concat(
        getSingletonRoute(),
        getEntryRoute(),
        putEntryRoute()
    );
  }

  private Route getEntryRoute() {
    PathMatcher2<String, String> pathMatcher =
        PathMatchers
            .segment("kvstorage")
            .slash(segment())
            .slash(segment());

    Route route = path(
        pathMatcher,
        (tableName, key) -> onSuccess(
            getTableEntry(tableName, key),
            (Message t) -> complete(StatusCodes.OK, t, Jackson.<Message>marshaller())
        )
    );

    return get(() -> route);
  }

  private Route putEntryRoute() {
    PathMatcher2<String, String> pathMatcher =
        PathMatchers
            .segment("kvstorage")
            .slash(segment())
            .slash(segment());

    final Unmarshaller<HttpEntity, PutRequest> unmarshaller =
        Jackson.<PutRequest>unmarshaller(PutRequest.class);

    Route route = path(
        pathMatcher,
        (table, key) -> put(
            () -> entity(
                unmarshaller,
                msg -> putRequestHandler(msg, table, key)
            )
        )
    );

    return route;
  }

  private Route putRequestHandler(PutRequest msg, String table, String key) {
    ActorSelection selection = system.actorSelection("/system/sharding/storageProxy");

    selection.tell(
        new PutMessage<>(table, key, msg.getValue()),
        ActorRef.noSender()
    );

    return complete(StatusCodes.OK);
  }

  private CompletableFuture<String> putTableEntry(String tableName, String key, String value) {
    return null;
  }

  private Route getSingletonRoute() {
    return get(
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
  }

  private CompletableFuture<Message> getTableEntry(String tableName, String key) {
    ActorSelection selection = system.actorSelection("/system/sharding/storageProxy");

    CompletableFuture<Object> future = ask(
        selection,
        new GetMessage<>(tableName, key),
        Duration.ofMillis(1000)
    ).toCompletableFuture().exceptionally((t) -> null);

    return future.thenCompose(obj ->
        CompletableFuture.supplyAsync(() ->
            (Message) obj
        )
    );
  }

  private CompletableFuture<Message> getSingletonLocation() {
    ActorSelection selection = system.actorSelection("/user/singletonChecker");

    CompletableFuture<Object> future = ask(
        selection,
        new HasSingleton(),
        Duration.ofMillis(1000)
    ).toCompletableFuture().exceptionally((t) -> null);

    return future.thenCompose(obj ->
        CompletableFuture.supplyAsync(() ->
            new AddressReply(obj.toString())
        )
    );
  }
}
