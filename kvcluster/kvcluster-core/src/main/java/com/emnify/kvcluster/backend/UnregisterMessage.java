package com.emnify.kvcluster.backend;

import akka.actor.ActorRef;

/**
 *
 * @author Danilo Oliveira
 */
class UnregisterMessage {
    private final String key;
    private final ActorRef actor;
    
    public UnregisterMessage(String key, ActorRef actor) {
        this.key = key;
        this.actor = actor;
    }

    public ActorRef actor() {
        return actor;
    }

    public String key() {
        return key;
    }


    
}
