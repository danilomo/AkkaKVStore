package com.emnify.kvcluster.api;

import java.util.Optional;
import java.util.function.Consumer;

public class SafeCast<T> {
  private final Class<T> clazz;

  public SafeCast(Class<T> clazz) {
    this.clazz = clazz;
  }

  public void when(Object obj, Consumer<T> consumer){
    if(assignable(obj)){
      consumer.accept((T) obj);
    }
  }

  public Optional<T> cast(Object obj){
    if(assignable(obj)){
      return Optional.of((T) obj);
    }

    return Optional.empty();
  }

  private boolean assignable(Object obj) {
    return clazz.isAssignableFrom(clazz);
  }
}
