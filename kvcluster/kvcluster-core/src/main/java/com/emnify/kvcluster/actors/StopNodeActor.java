package com.emnify.kvcluster.actors;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import com.emnify.kvcluster.messages.StopNodeMessage;

/**
 *
 * @author Danilo Oliveira
 */
public class StopNodeActor extends AbstractActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StopNodeMessage.class, (m) -> unregisterNodeAndTerminate())
                .build();
    }

    private void unregisterNodeAndTerminate() {
        Cluster cluster = Cluster.get(context().system());
        cluster.leave(cluster.selfAddress());
        context().system().terminate();//.onComplete(
//            (Try<Terminated> v1) -> {
//                System.exit(0);
//                return null;
//            },
//            ExecutionContext.global()
//        );
    }

}
