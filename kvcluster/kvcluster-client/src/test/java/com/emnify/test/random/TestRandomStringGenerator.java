package com.emnify.test.random;

import com.emnify.kvcluster.client.random.RandomStringFromList;
import com.emnify.kvcluster.client.random.StringGenerator;
import java.util.stream.IntStream;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Danilo Oliveira
 */
public class TestRandomStringGenerator {

    @Test
    public void testUniformStringGeneration() {
        String[] stringArray = {"0", "1", "2", "3", "4", "5"};
        int[] ocurrences = {0, 0, 0, 0, 0, 0};

        int iterations = 10000;

        StringGenerator generator = new RandomStringFromList(stringArray);

        IntStream.range(1, iterations).forEach(i -> {
            int random = Integer.parseInt(generator.generate());
            ocurrences[random]++;
        });

        for (int value : ocurrences) {
            double expected = 1.0 / 6.0;
            double got = ((double) value) / iterations;
            double relativeError = Math.abs((expected - got) / expected);
            System.out.println(relativeError);
            assertTrue(relativeError < 0.1);
        }

    }
}
