package com.emnify.kvcluster.backend;

import com.emnify.kvcluster.api.SafeCast;
import com.emnify.kvcluster.messages.RequestMessage;

import akka.cluster.sharding.ShardRegion;

public class MessageExtractor implements ShardRegion.MessageExtractor{

  private final static int SHARD_REGIONS = 100;

  private SafeCast<RequestMessage> caster = new SafeCast<>(RequestMessage.class);

  @Override
  public String entityId(Object message) {
    return caster
        .cast(message)
        .map( msg -> msg.table() )
        .orElseThrow( () -> new RuntimeException("Not supported"));
  }

  @Override
  public Object entityMessage(Object message) {
    return caster
        .cast(message)
        .orElseThrow( () -> new RuntimeException("Not supported"));
  }

  @Override
  public String shardId(Object message) {
    return String.valueOf(entityId(message).hashCode() % SHARD_REGIONS);
  }
}
