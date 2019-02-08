/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emnify.kvcluster.messages;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Accessors(fluent = true) @Getter
@EqualsAndHashCode(callSuper=false)
@ToString
public class TakeMessage<K> extends RequestMessage{
    private final K key;
    private final int timeout;
}