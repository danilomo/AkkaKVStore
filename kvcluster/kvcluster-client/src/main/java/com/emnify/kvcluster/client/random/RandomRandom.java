package com.emnify.kvcluster.client.random;

import com.emnify.kvcluster.api.ConfigLocalAddress;
import com.typesafe.config.ConfigFactory;

/**
 * @author danilo
 */
public class RandomRandom {
    public static void main(String[] args) {
        System.out.println(">>>>" + new ConfigLocalAddress(ConfigFactory.load("client")).get() + "<<<");
    }
}
