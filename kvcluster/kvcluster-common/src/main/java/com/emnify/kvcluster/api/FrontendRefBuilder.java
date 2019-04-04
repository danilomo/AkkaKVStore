package com.emnify.kvcluster.api;

import akka.actor.ActorRef;
import akka.actor.ActorRefFactory;
import akka.routing.Group;
import com.typesafe.config.Config;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Danilo Oliveira
 */
public class FrontendRefBuilder {

    private final ActorRefFactory context;
    private List<String> paths;
    private Function<List<String>, Group> group;
    private String name;
    private Config config;

    public FrontendRefBuilder(ActorRefFactory context) {
        this.context = context;
        this.name = "router";
    }

    public FrontendRefBuilder withPaths(List<String> paths) {
        this.paths = paths;
        return this;
    }

    public FrontendRefBuilder withConfig(Config config) {
        this.config = config;
        return this;
    }

    public FrontendRefBuilder withStringList(String propertyName,
                                             Function<String, String> transformation) {
        paths = config
            .getStringList(propertyName)
            .stream()
            .map(transformation)
            .collect(Collectors.toList());

        return this;
    }

    public FrontendRefBuilder withGroup(Function<List<String>, Group> group) {
        this.group = group;
        return this;
    }

    public FrontendRefBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ActorRef build() {
        return context.actorOf(group.apply(paths).props(), name);
    }

}
