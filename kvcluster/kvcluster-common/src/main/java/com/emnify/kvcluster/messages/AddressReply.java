package com.emnify.kvcluster.messages;

import akka.actor.Address;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Accessors
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class AddressReply extends ReplyMessage {
    private String address;
}
