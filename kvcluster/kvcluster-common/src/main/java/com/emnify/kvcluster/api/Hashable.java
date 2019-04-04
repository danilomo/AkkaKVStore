package com.emnify.kvcluster.api;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author Danilo Oliveira
 */
public abstract class Hashable {

    private static final BigInteger MAX_INT = new BigInteger(String.valueOf(Integer.MAX_VALUE));

    public static Hashable of(String string) {
        return new ByteArrayHash(string);
    }

    public static Hashable of(byte[] bytes) {
        return new ByteArrayHash(bytes);
    }

    public static Hashable of(UUID uuid) {
        return new UUIDHash(uuid);
    }

    public static void main(String[] args) {
        System.out.println(Hashable.of("a").hashCode());
        System.out.println(Hashable.of("b").hashCode());
        System.out.println(Hashable.of("c").hashCode());

        System.out.println("");

        System.out.println("a".hashCode());
        System.out.println("b".hashCode());
        System.out.println("c".hashCode());
    }

    public abstract byte[] toBytes();

    @Override
    public int hashCode() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = md.digest(toBytes());

            return new BigInteger(hashInBytes).mod(MAX_INT).intValue();
        } catch (NoSuchAlgorithmException ex) {
            return new String(toBytes()).hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof Hashable) {
            Hashable hasheable = (Hashable) obj;
            return Arrays.equals(this.toBytes(), hasheable.toBytes());
        }
        return false;
    }

    private static class UUIDHash extends Hashable {
        private final byte[] array;
        private final UUID uuid;

        public UUIDHash(UUID uuid) {
            ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
            bb.putLong(uuid.getMostSignificantBits());
            bb.putLong(uuid.getLeastSignificantBits());

            this.array = bb.array();
            this.uuid = uuid;
        }

        @Override
        public byte[] toBytes() {
            return array;
        }

        @Override
        public String toString() {
            return uuid.toString();
        }
    }

    private static class ByteArrayHash extends Hashable {

        private final byte[] array;
        private final String stringRep;

        public ByteArrayHash(byte[] array) {
            this.array = array;
            this.stringRep = new String(array);
        }

        private ByteArrayHash(String rep) {
            this.array = rep.getBytes();
            this.stringRep = rep;
        }

        @Override
        public byte[] toBytes() {
            return array;
        }

        @Override
        public String toString() {
            return stringRep;
        }
    }
}
