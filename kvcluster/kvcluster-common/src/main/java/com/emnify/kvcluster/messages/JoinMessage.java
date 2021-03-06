package com.emnify.kvcluster.messages;

import akka.actor.ActorRef;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.UUID;

/**
 * @author Danilo Oliveira
 */
@AllArgsConstructor
@Accessors(fluent = true)
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class JoinMessage extends Message {
    private final UUID uuid;
    private final ActorRef actor;
}
