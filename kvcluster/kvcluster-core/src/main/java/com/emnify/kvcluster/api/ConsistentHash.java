package com.emnify.kvcluster.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHash<T> {
    private final HashFunction hashFunction;
    private final int numberOfReplicas;
    private final SortedMap<Integer, T> circle
            = new TreeMap<>();

    public ConsistentHash(HashFunction hashFunction,
            int numberOfReplicas, Collection<T> nodes) {

        this.hashFunction = hashFunction;
        this.numberOfReplicas = numberOfReplicas;

        nodes.forEach((node) -> {
            add(node);
        });
    }

    public void add(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.put(hashFunction.hash(node.toString() + i),
                    node);
        }
    }

    public void remove(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.remove(hashFunction.hash(node.toString() + i));
        }
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
    
    public static void main(String[] args) {
        ConsistentHash<String> ch = new ConsistentHash<>( f -> f.hashCode(), 20, Arrays.asList("pera", "uva", "maca", "salada"));
        
        String[] str1 = {"Acre","Alagoas","Amapá","Amazonas","Bahia","Ceará","Distrito Federal","Espírito Santo","Goiás","Maranhão","Mato Grosso","Mato Grosso do Sul","Minas Gerais","Pará","Paraíba","Paraná","Pernambuco","Piauí","Rio de Janeiro","Rio Grande do Norte","Rio Grande do Sul","Rondônia","Roraima","Santa Catarina","São Paulo","Sergipe","Tocantins"};
        
        for(String s: str1){
            System.out.println(s + " -> " + ch.get(s));
        }
        
        ch.remove("maca");
        
        System.out.println("");
        
        for(String s: str1){
            System.out.println(s + " -> " + ch.get(s));
        }
        
        
    }

}
