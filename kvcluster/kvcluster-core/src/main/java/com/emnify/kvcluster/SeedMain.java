package com.emnify.kvcluster;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;

public class SeedMain {

  public static void main(String[] args) {
    int port = 2551;

    Config config = ConfigFactory.parseString(
        "akka.remote.netty.tcp.port=" + port)
        .withFallback(ConfigFactory.load("seed"));

    ActorSystem system = ActorSystem.create("kvstore", config);
  }

}
