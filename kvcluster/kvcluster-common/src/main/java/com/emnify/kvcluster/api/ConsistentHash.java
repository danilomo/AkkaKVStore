package com.emnify.kvcluster.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

        
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

    public static void main(String[] args) {

//        ConsistentHash<String> ch = new ConsistentHash<>(f -> f.hashCode(), 20, Arrays.asList("sdlfkasjflawe", "lsnvwkere", "sadfjLSKJDLFJE"));
//
//        String[] str1 = {"Acre", "Alagoas", "Amapá", "Amazonas", "Bahia", "Ceará", "Distrito Federal", "Espírito Santo", "Goiás", "Maranhão", "Mato Grosso", "Mato Grosso do Sul", "Minas Gerais", "Pará", "Paraíba", "Paraná", "Pernambuco", "Piauí", "Rio de Janeiro", "Rio Grande do Norte", "Rio Grande do Sul", "Rondônia", "Roraima", "Santa Catarina", "São Paulo", "Sergipe", "Tocantins"};
//
//        printBags(ch, str1);
//
//        ch.add("fjlaskjeoiur");
//
//        printBags(ch, str1);

        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID uuid3 = UUID.randomUUID();
        UUID uuid4 = UUID.randomUUID();        
        
        List<Hashable> list = Arrays
                .asList(uuid1, uuid2, uuid3, uuid4)
                .stream()
                .map( Hashable::of )
                .collect(Collectors.toList());
        
        ConsistentHash<Hashable> ch = new ConsistentHash<>(f -> f.hashCode(), 200, list );

        String[] str1 = {"Acre", "Alagoas", "Amapá", "Amazonas", "Bahia", "Ceará", "Distrito Federal", "Espírito Santo", "Goiás", "Maranhão", "Mato Grosso", "Mato Grosso do Sul", "Minas Gerais", "Pará", "Paraíba", "Paraná", "Pernambuco", "Piauí", "Rio de Janeiro", "Rio Grande do Norte", "Rio Grande do Sul", "Rondônia", "Roraima", "Santa Catarina", "São Paulo", "Sergipe", "Tocantins"};

        printBags(ch, str1);

        ch.remove( Hashable.of(uuid3) );
        System.out.println("Removed: " + uuid3);

        printBags(ch, str1);

    }

    private static void printBags(ConsistentHash<Hashable> ch, String[] arr) {
        Map<Hashable, List<String>> map = new HashMap<>();

        ch.nodes.forEach((node) -> {
            map.put(node, new ArrayList<>());
        });

        for (String str : arr) {
            map.get(ch.get(str)).add(str);
        }

        System.out.println(map);
        System.out.println("---");
    }

}
