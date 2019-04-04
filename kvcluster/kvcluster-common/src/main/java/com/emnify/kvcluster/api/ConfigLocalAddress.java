package com.emnify.kvcluster.api;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.function.Supplier;

/**
 * @author danilo
 */
public class ConfigLocalAddress implements Supplier<String> {
    private final Config config;

    public ConfigLocalAddress(Config config) {
        this.config = config;
    }

    public static void main(String[] args) {
        System.out.println(">>>>" + new ConfigLocalAddress(ConfigFactory.load("client")).get() + "<<<");
    }

    @Override
    public String get() {
        return config.getString("akka.remote.netty.tcp.hostname");
    }


}
