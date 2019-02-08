package com.emnify.kvcluster.playground;

import akka.routing.ConsistentHashingRouter;

/**
 *
 * @author danilo
 */
public class Msg implements ConsistentHashingRouter.ConsistentHashable {
    private final String str;

    public Msg(String str) {
        this.str = str;
    }

    public String str() {
        return str;
    }

    @Override
    public int hashCode() {
        return str.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Msg) {
            return ((Msg) obj).str.equals(this.str);
        }

        return false;
    }

    @Override
    public Object consistentHashKey() {
        return str;
    }

    @Override
    public String toString() {
        return str;
    }
    
    
}
