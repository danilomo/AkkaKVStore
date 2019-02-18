package com.emnify.kvcluster.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

        
public class ConsistentHash<T> {

    private final HashFunction hashFunction;
    private final int numberOfReplicas;
    private final SortedMap<Integer, T> circle
            = new TreeMap<>();
    private final List<T> nodes;
    

    public ConsistentHash(HashFunction hashFunction,
            int numberOfReplicas, Collection<T> nodes) {
        this.hashFunction = hashFunction;
        this.numberOfReplicas = numberOfReplicas;
        this.nodes = new ArrayList<>();
        this.nodes.addAll(nodes);

        nodes.forEach((node) -> {
            addAux(node);
        });
    }

    public void add(T node) {
        addAux(node);
        this.nodes.add(node);
    }

    private void addAux(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.put(hashFunction.hash(node.toString() + i),
                    node);
        }
    }

    public void remove(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.remove(hashFunction.hash(node.toString() + i));
        }
        this.nodes.remove(node);
    }

    public T get(Object key) {
        if (circle.isEmpty()) {
            return null;
        }
        int hash = hashFunction.hash(key);
        if (!circle.containsKey(hash)) {
            SortedMap<Integer, T> tailMap
                    = circle.tailMap(hash);
            hash = tailMap.isEmpty()
                    ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }

}
