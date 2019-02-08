package com.emnify.kvcluster.frontend;

import akka.actor.AbstractActor;
import com.emnify.kvcluster.api.ConsistentHash;
import com.emnify.kvcluster.messages.GetMessage;
import com.emnify.kvcluster.messages.JoinMessage;
import com.emnify.kvcluster.messages.PutMessage;
import static com.emnify.kvcluster.api.CustomLogger.println;

/**
 *
 * @author Danilo Oliveira
 */
public class FrontendActor extends AbstractActor {

    ConsistentHash<String> consistentHash;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JoinMessage.class, this::joinMessage)
                .match(PutMessage.class, this::putMessage)
                .match(GetMessage.class, this::getMessage)
                .build();
    }

    private void joinMessage(JoinMessage message) {
        println("/home/danilo/logfile.txt", self() + " -> " + message + "\n");
    }

    private void putMessage(PutMessage<String, String> message) {

    }

    private void getMessage(GetMessage<String> message) {

    }

}
