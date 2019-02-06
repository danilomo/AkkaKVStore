package com.emnify.kvcluster.frontend;

import akka.actor.AbstractActor;

/**
 *
 * @author Danilo Oliveira
 */
public class FrontendActor extends AbstractActor{

    @Override
    public Receive createReceive() {
        return receiveBuilder().build();
    }
    
}
