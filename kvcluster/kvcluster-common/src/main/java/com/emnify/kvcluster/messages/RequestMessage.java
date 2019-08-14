package com.emnify.kvcluster.messages;

import akka.routing.ConsistentHashingRouter;

/**
 * @author Danilo Oliveira
 */
public abstract class RequestMessage extends Message
    implements ConsistentHashingRouter.ConsistentHashable  {
  public abstract String table();

  @Override
  public Object consistentHashKey() {
    return table();
  }
}
