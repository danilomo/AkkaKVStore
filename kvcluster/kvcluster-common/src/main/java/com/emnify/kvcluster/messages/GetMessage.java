package com.emnify.kvcluster.messages;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Accessors(fluent = true)
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class GetMessage<K> extends RequestMessage {
    private final String table;
    private final K key;

    public GetMessage(K key) {
        this.key = key;
        this.table = "";
    }
}