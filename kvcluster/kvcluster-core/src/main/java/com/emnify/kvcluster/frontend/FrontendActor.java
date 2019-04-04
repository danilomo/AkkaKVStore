package com.emnify.kvcluster.frontend;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Address;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import com.emnify.kvcluster.api.ConsistentHash;
import com.emnify.kvcluster.api.CustomLogger;
import com.emnify.kvcluster.messages.GetMessage;
import com.emnify.kvcluster.messages.JoinMessage;
import com.emnify.kvcluster.messages.PutMessage;
import com.emnify.kvcluster.messages.TakeMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Danilo Oliveira
 */
public class FrontendActor extends AbstractActor {

    private final ConsistentHash<UUID> consistentHash;
    private final Map<UUID, ActorRef> nodes;
    private final Map<Address, UUID> addresses;
    private Cluster cluster;

    public FrontendActor() {
        consistentHash = new ConsistentHash<>(obj -> obj.toString().hashCode(), 1024, new ArrayList<>());
        nodes = new HashMap<>();
        addresses = new HashMap<>();
    }

    @Override
    public void preStart() throws Exception {
        cluster = Cluster.get(getContext().getSystem());
        cluster.subscribe(
            getSelf(),
            ClusterEvent.initialStateAsEvents(),
            MemberEvent.class,
            MemberRemoved.class
        );
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(JoinMessage.class, this::joinMessage)
            .match(PutMessage.class, this::putMessage)
            .match(GetMessage.class, this::getMessage)
            .match(TakeMessage.class, this::takeMessage)
            .match(MemberRemoved.class, this::memberRemoved)
            .build();
    }

    private void joinMessage(JoinMessage message) {
        consistentHash.add(message.uuid());
        nodes.put(message.uuid(), message.actor());
        addresses.put(message.actor().path().address(), message.uuid());

        debug();
    }

    private void putMessage(PutMessage<String, String> message) {
        lookupActor(message.key()).tell(message, sender());
    }

    private void getMessage(GetMessage<String> message) {
        lookupActor(message.key()).tell(message, sender());
    }

    private void takeMessage(TakeMessage<String> message) {
        lookupActor(message.key()).tell(message, sender());
    }

    private void memberRemoved(MemberRemoved message) {
        Address addr = message.member().address();
        if (addresses.containsKey(addr)) {
            UUID uuid = addresses.get(addr);

            consistentHash.remove(uuid);
            addresses.remove(addr);
            nodes.remove(uuid);

            debug();
        }


    }

    private ActorRef lookupActor(String key) {
        UUID uuid = consistentHash.get(key);
        ActorRef actor = nodes.get(uuid);
        return actor;
    }

    private void debug() {
        CustomLogger.println(nodes.toString());
        CustomLogger.println(addresses.toString());
        CustomLogger.println("");
    }

}
