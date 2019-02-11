package com.emnify.kvcluster.client.random;

import java.util.Random;
import java.util.stream.IntStream;

/**
 *
 * @author danilo
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
        return  Math.log(1-random.nextDouble())/(-lambda);
    }
    
    public static void main(String[] args) {
        RandomGenerator gen = new ExponentialGenerator(1/10.0);
        double sum = IntStream.range(1, 10000)
                .asDoubleStream()
                .map( val -> gen.generate() )
                .reduce( (v1, v2) -> v1 + v2 ).getAsDouble();
        
        System.out.println(sum/10000);
    }

}
