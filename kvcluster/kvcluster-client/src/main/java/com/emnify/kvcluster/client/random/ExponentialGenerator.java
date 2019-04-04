package com.emnify.kvcluster.client.random;

import java.util.Random;

/**
 * @author Danilo Oliveira
 */
public class ExponentialGenerator implements RandomGenerator {

    private final double lambda;
    private final Random random;

    public ExponentialGenerator(double lambda) {
        this.lambda = lambda;
        this.random = new Random();
    }

    @Override
    public double generate() {
        return Math.log(1 - random.nextDouble()) / (-lambda);
    }

}
