package com.emnify.test.random;

import com.emnify.kvcluster.client.random.ExponentialGenerator;
import com.emnify.kvcluster.client.random.RandomGenerator;
import java.util.stream.IntStream;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author danilo
 */
public class TestExponentialGenerator {

    @Test
    public void testAverage() {
        RandomGenerator gen = new ExponentialGenerator(1 / 10.0);
        double sum = IntStream.range(1, 10000)
                .asDoubleStream()
                .map(val -> gen.generate())
                .reduce((v1, v2) -> v1 + v2).getAsDouble();
        
        double average = sum/10000.0;
        double relativeError = Math.abs((average - 10)/10);
        System.out.println(relativeError);
        assertTrue(relativeError < 0.1);
    }
}
