package com.emnify.kvcluster.messages;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @param <K> Key
 * @param <V> Value
 * @author Danilo Oliveira
 */
@AllArgsConstructor
@Accessors(fluent = true)
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class PutMessage<K, V> extends RequestMessage {
    private final String table;
    private final K key;
    private final V value;

    public PutMessage(K key, V value) {
        this.key = key;
        this.value = value;
        this.table = "";
    }
}
