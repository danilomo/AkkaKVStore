package com.emnify.kvcluster.api;

/**
 * @author Danilo Oliveira
 */
@FunctionalInterface
public interface HashFunction {
    public int hash(Object obj);
}
