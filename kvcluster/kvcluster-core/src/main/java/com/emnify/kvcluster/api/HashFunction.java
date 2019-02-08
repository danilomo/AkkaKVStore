package com.emnify.kvcluster.api;

/**
 *
 * @author danilo
 */
@FunctionalInterface
public interface HashFunction {
    public int hash(Object obj);
}
