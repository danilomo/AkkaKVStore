package com.emnify.kvcluster.client.random;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Danilo Oliveira
 */
public class RandomStringFromList implements StringGenerator{
    private final List<String> list;
    private final Random random;

    public RandomStringFromList(List<String> list) {
        this.list = list;
        this.random = new Random();
    }

    public RandomStringFromList(String[] array) {
        this(Arrays.asList(array));
    }

    @Override
    public String generate() {
        int position = random.nextInt(list.size());
        return list.get(position);
    }
    
}
